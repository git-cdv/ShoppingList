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
import com.chkan.billing.domain.model.BillingError
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class SubscriptionBillingService @Inject constructor(
    private val context: Context
) : PurchasesUpdatedListener {

    private var billingClient: BillingClient? = null
    private var isConnecting = false

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

    private fun initializeBillingClient() {
        if (billingClient == null) {
            billingClient = newBuilder(context)
                .setListener(this)
                .enablePendingPurchases(
                    PendingPurchasesParams.newBuilder().build()
                )
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

    // Главный listener - обрабатывает ВСЕ события покупок
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingResponseCode.OK -> {
                // Фильтруем только подписки и отправляем в Flow
                val subscriptionPurchases = purchases?.filter { purchase ->
                    purchase.products.isNotEmpty() // Подписки всегда имеют productId
                } ?: emptyList()

                _purchasesFlow.tryEmit(Result.success(subscriptionPurchases))
            }
            else -> {
                val error = getBillingError(billingResult.responseCode)
                _purchasesFlow.tryEmit(Result.failure(Exception(error.description)))
            }
        }
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

    // Проверка состояния покупки перед acknowledge
    suspend fun acknowledgePurchase(
        purchase: Purchase
    ): BillingResult = suspendCancellableCoroutine { continuation ->

        //Проверяем состояние покупки перед acknowledge
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED || purchase.isAcknowledged) {
            continuation.resume(
                BillingResult.newBuilder()
                    .setResponseCode(BillingResponseCode.DEVELOPER_ERROR)
                    .setDebugMessage("Purchase is not in PURCHASED state or already acknowledged")
                    .build()
            )
            return@suspendCancellableCoroutine
        }

        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            continuation.resume(billingResult)
        } ?: continuation.resume(
            BillingResult.newBuilder()
                .setResponseCode(BillingResponseCode.SERVICE_DISCONNECTED)
                .build()
        )
    }

    // ✅ Overload для обратной совместимости
    suspend fun acknowledgePurchase(purchaseToken: String): BillingResult {
        // Сначала находим покупку по токену
        val (queryResult, purchases) = querySubscriptionPurchases()
        if (queryResult.responseCode != BillingResponseCode.OK || purchases == null) {
            return queryResult
        }

        val purchase = purchases.find { it.purchaseToken == purchaseToken }
            ?: return BillingResult.newBuilder()
                .setResponseCode(BillingResponseCode.DEVELOPER_ERROR)
                .setDebugMessage("Purchase with token $purchaseToken not found")
                .build()

        return acknowledgePurchase(purchase)
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
}
