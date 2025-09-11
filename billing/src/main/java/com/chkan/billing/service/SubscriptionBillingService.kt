package com.chkan.billing.service

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.*
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.chkan.billing.core.BillingLogger
import com.chkan.billing.di.ApplicationScope
import com.chkan.billing.di.Dispatcher
import com.chkan.billing.di.DispatcherType
import com.chkan.billing.domain.model.BillingError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.math.min

@Singleton
class SubscriptionBillingService @Inject constructor(
    private val context: Context,
    @Dispatcher(DispatcherType.IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
    private val logger: BillingLogger
) : PurchasesUpdatedListener {

    private var billingClient: BillingClient? = null
    private var isConnecting = false
    private val purchasesJobs = ConcurrentHashMap<String, Job>()

    private val _purchasesFlow = MutableSharedFlow<Result<List<Purchase>>>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val activeSubscriptionsFlow: Flow<Result<List<Purchase>>> = _purchasesFlow.asSharedFlow()

    private val _connectionStateFlow = MutableSharedFlow<Result<Boolean>>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val connectionStateFlow: Flow<Result<Boolean>> = _connectionStateFlow.asSharedFlow()

    //INIT CONNECTION AND GET ACTIVE SUBSCRIPTIONS FLOW
    private fun initializeBillingClient() {
        if (billingClient == null) {
            val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()


            billingClient = newBuilder(context)
                .setListener(this)
                .enablePendingPurchases(pendingPurchasesParams)
                .enableAutoServiceReconnection()
                .build()
        }
    }

    fun startConnection() {
        initializeBillingClient()

        // Проверяем, не подключены ли уже
        val client = billingClient
        if (client?.isReady == true) {
            _connectionStateFlow.tryEmit(Result.success(true))
            return
        }

        // Проверяем, не в процессе ли подключения
        if (isConnecting) {
            return
        }

        isConnecting = true
        client?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                isConnecting = false
                val isConnected = billingResult.responseCode == BillingResponseCode.OK

                if (isConnected) {
                    _connectionStateFlow.tryEmit(Result.success(true))
                    // Загружаем текущие покупки при подключении
                    queryAndEmitCurrentPurchases()
                } else {
                    val error = getBillingError(billingResult.responseCode)
                    _connectionStateFlow.tryEmit(Result.failure(Exception(error.description)))
                }
            }

            override fun onBillingServiceDisconnected() {
                _connectionStateFlow.tryEmit(Result.failure(Exception(BillingError.SERVICE_DISCONNECTED.description)))
            }
        })
    }

    private fun queryAndEmitCurrentPurchases() {
        val client = billingClient
        if (client?.isReady == true) {
            client.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(ProductType.SUBS)
                    .build()
            ) { billingResult, purchasesList ->
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    _purchasesFlow.tryEmit(Result.success(purchasesList))
                }
            }
        }
    }

    fun endConnection() {
        billingClient?.let { client ->
            if (client.isReady) {
                client.endConnection()
            }
        }
        billingClient = null
        isConnecting = false
        _connectionStateFlow.tryEmit(Result.success(false))
    }

    //INIT PURCHASE FLOW
    /**
     * Запускает диалог покупки подписки.
     * ВАЖНО: Возвращает результат запуска диалога, НЕ результат покупки!
     * Результат покупки придет в onPurchasesUpdated -> subscriptionPurchasesFlow
     */
    suspend fun launchSubscriptionFlow(
        activity: Activity,
        productDetails: ProductDetails,
        offerToken: String
    ): BillingResult = suspendCancellableCoroutine { continuation ->

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val client = billingClient
        if (client == null || !client.isReady) {
            continuation.resume(
                BillingResult.newBuilder()
                    .setResponseCode(BillingResponseCode.SERVICE_DISCONNECTED)
                    .build()
            )
            return@suspendCancellableCoroutine
        }

        val billingResult = client.launchBillingFlow(activity, billingFlowParams)
        continuation.resume(billingResult)
    }

    // Главный listener - обрабатывает ВСЕ события покупок
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingResponseCode.OK -> {
                if (!purchases.isNullOrEmpty()) {
                    scope.launch(ioDispatcher) {
                        try {
                            processPurchases(purchases)
                        } catch (e: Exception) {
                            logger.e(e, "Error processing purchases")
                        }
                    }
                    val readyPurchases = purchases.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                    _purchasesFlow.tryEmit(Result.success(readyPurchases))
                }
            }
            else -> {
                val error = getBillingError(billingResult.responseCode)
                _purchasesFlow.tryEmit(Result.failure(Exception(error.description)))
            }
        }
    }

    private fun processPurchases(purchases: List<Purchase>) {
        purchases.forEach { purchase ->
            val existingJob = purchasesJobs[purchase.purchaseToken]
            if (existingJob?.isActive != true) {
                purchasesJobs[purchase.purchaseToken] = scope.launch(ioDispatcher) {
                    try {
                        processPurchase(purchase)
                    } catch (e: Exception) {
                        logger.e(e, "Error processing purchase ${purchase.purchaseToken}")
                    } finally {
                        purchasesJobs.remove(purchase.purchaseToken)
                    }
                }
            }
        }
    }

    private suspend fun processPurchase(purchase: Purchase) {
        val productId = purchase.products.firstOrNull() ?: "unknown"

        logger.d(TAG,"Processing purchase: $productId, state: ${purchase.purchaseState}")

        when (purchase.purchaseState) {
            Purchase.PurchaseState.PENDING -> {
                logger.d(TAG,"Purchase $productId is pending")
                return
            }

            Purchase.PurchaseState.UNSPECIFIED_STATE -> {
                logger.d(TAG,"Purchase $productId has unspecified state")
                return
            }

            Purchase.PurchaseState.PURCHASED -> {
                if (purchase.isAcknowledged) {
                    logger.d(TAG,"Purchase $productId already acknowledged")
                } else {
                    acknowledgePurchase(purchase.purchaseToken)
                }
            }
        }
    }

    private suspend fun acknowledgePurchase(purchaseToken: String) {
        var currentDelay = INITIAL_RETRY_DELAY_MS
        val maxRetries = MAX_RETRY_ATTEMPTS

        repeat(maxRetries) { attempt ->
            try {
                val result = withContext(ioDispatcher) {
                    val params = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchaseToken)
                        .build()
                    billingClient?.acknowledgePurchase(params)
                }

                when (result?.responseCode) {
                    BillingResponseCode.OK -> {
                        logger.d(TAG,"Purchase $purchaseToken acknowledged successfully")
                        return
                    }

                    BillingResponseCode.ITEM_NOT_OWNED -> {
                        logger.d(TAG,"Acknowledgment failed: item not owned")
                        // Refresh purchases and try again
                        val (billingResult, purchasesList) = querySubscriptionPurchases()
                        purchasesList?.find { it.purchaseToken == purchaseToken }
                            ?.let { freshPurchase ->
                                if (!freshPurchase.isAcknowledged) {
                                    delay(currentDelay)
                                    currentDelay = min(currentDelay * 2, 30_000L)
                                    return@repeat
                                }
                            }
                        return // Item not found or already acknowledged
                    }

                    in RETRYABLE_ERRORS -> {
                        if (attempt < maxRetries - 1) {
                            logger.d(TAG,"Acknowledgment failed (attempt ${attempt + 1}), retrying...")
                            delay(currentDelay)
                            currentDelay = min(currentDelay * 2, 30_000L)
                        } else {
                            throw Exception("Acknowledgment failed")
                        }
                    }
                    else -> {
                        throw Exception("Acknowledgment failed")
                    }
                }
            } catch (e: Exception) {
                if (attempt == maxRetries - 1) throw e
                delay(currentDelay)
                currentDelay = min(currentDelay * 2, 30_000L)
            }
        }
    }

    suspend fun querySubscriptionDetails(
        productIds: List<String>
    ): Pair<BillingResult, List<ProductDetails>?> = suspendCancellableCoroutine { continuation ->

        val client = billingClient
        if (client == null || !client.isReady) {
            continuation.resume(
                Pair(
                    BillingResult.newBuilder()
                        .setResponseCode(BillingResponseCode.SERVICE_DISCONNECTED)
                        .build(),
                    null
                )
            )
            return@suspendCancellableCoroutine
        }

        val productList = productIds.map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(ProductType.SUBS)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        client.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
            val productDetailsList = queryProductDetailsResult.productDetailsList
            continuation.resume(Pair(billingResult, productDetailsList))
        }
    }

    suspend fun querySubscriptionPurchases(): Pair<BillingResult, List<Purchase>?> =
        suspendCancellableCoroutine { continuation ->
            val client = billingClient
            if (client == null || !client.isReady) {
                continuation.resume(
                    Pair(
                        BillingResult.newBuilder()
                            .setResponseCode(BillingResponseCode.SERVICE_DISCONNECTED)
                            .build(),
                        null
                    )
                )
                return@suspendCancellableCoroutine
            }

            client.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(ProductType.SUBS)
                    .build()
            ) { billingResult, purchasesList ->
                continuation.resume(Pair(billingResult, purchasesList))
            }
        }


    fun getBillingError(responseCode: Int): BillingError {
        return when (responseCode) {
            BillingResponseCode.SERVICE_TIMEOUT -> BillingError.SERVICE_TIMEOUT
            BillingResponseCode.FEATURE_NOT_SUPPORTED -> BillingError.FEATURE_NOT_SUPPORTED
            BillingResponseCode.SERVICE_DISCONNECTED -> BillingError.SERVICE_DISCONNECTED
            BillingResponseCode.OK -> BillingError.OK
            BillingResponseCode.USER_CANCELED -> BillingError.USER_CANCELED
            BillingResponseCode.SERVICE_UNAVAILABLE -> BillingError.SERVICE_UNAVAILABLE
            BillingResponseCode.BILLING_UNAVAILABLE -> BillingError.BILLING_UNAVAILABLE
            BillingResponseCode.ITEM_UNAVAILABLE -> BillingError.ITEM_UNAVAILABLE
            BillingResponseCode.DEVELOPER_ERROR -> BillingError.DEVELOPER_ERROR
            BillingResponseCode.ERROR -> BillingError.ERROR
            BillingResponseCode.ITEM_ALREADY_OWNED -> BillingError.ITEM_ALREADY_OWNED
            BillingResponseCode.ITEM_NOT_OWNED -> BillingError.ITEM_NOT_OWNED
            else -> BillingError.ERROR
        }
    }

    private companion object {
        private const val TAG = "Billing"
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val INITIAL_RETRY_DELAY_MS = 1000L

        private val RETRYABLE_ERRORS = setOf(
            BillingResponseCode.ERROR,
            BillingResponseCode.SERVICE_DISCONNECTED,
            BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingResponseCode.NETWORK_ERROR
        )
    }
}
