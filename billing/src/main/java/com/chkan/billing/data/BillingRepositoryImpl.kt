package com.chkan.billing.data

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.chkan.billing.core.BillingLogger
import com.chkan.billing.data.SubscriptionMapper.toDomainSubscription
import com.chkan.billing.data.SubscriptionMapper.toDomainSubscriptionPurchase
import com.chkan.billing.domain.BillingRepository
import com.chkan.billing.domain.error.PurchasesError
import com.chkan.billing.domain.error.PurchasesException
import com.chkan.billing.domain.error.toPurchasesException
import com.chkan.billing.domain.model.Subscription
import com.chkan.billing.domain.model.SubscriptionPurchase
import com.chkan.billing.service.SubscriptionBillingService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BillingRepositoryImpl @Inject constructor(
    private val billingService: SubscriptionBillingService,
    private val logger: BillingLogger
) : BillingRepository {

    override val activeSubscriptionsFlow: Flow<Result<List<SubscriptionPurchase>>> =
        billingService.activeSubscriptionsFlow.map { result ->
            if (result.isSuccess) {
                val purchases = result.getOrNull() ?: emptyList()
                Result.success(purchases.map { it.toDomainSubscriptionPurchase() })
            } else Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
        }

    override val billingConnectionState: Flow<Result<Boolean>> = billingService.connectionStateFlow

    override suspend fun querySubscriptions(productIds: List<String>): Result<List<Subscription>> {
        val (billingResult, productDetailsList) = billingService.querySubscriptionDetails(productIds)

        return if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList != null) {
            Result.success(productDetailsList.map { it.toDomainSubscription() })
        } else {
            val error = billingService.getBillingError(billingResult.responseCode)
            Result.failure(Exception(error.description))
        }
    }

    override suspend fun launchSubscriptionPurchase(
        activity: Activity,
        productId: String,
        offerToken: String?
    ) {
        val product = try {
            val (_, productDetailsList) = billingService.querySubscriptionDetails(listOf(productId))
            productDetailsList?.firstOrNull() ?: throw PurchasesException(
                PurchasesError.ProductNotAvailableForPurchaseError,
                message = "Product $productId not found"
            )
        } catch (t: Throwable) {
            throw t.toPurchasesException()
        }

        val selectedOfferToken = offerToken
            ?: product.subscriptionOfferDetails?.firstOrNull()?.offerToken
            ?: throw PurchasesException(
            PurchasesError.ProductNotAvailableForPurchaseError,
            message = "No offer token available: $productId"
        )

        try {
            billingService.launchSubscriptionFlow(
                activity,
                product,
                selectedOfferToken
            )
        } catch (e: Exception) {
            logger.e(e, "Purchase flow failed for product $productId")
            throw e.toPurchasesException()
        }
    }

    override suspend fun restorePurchases() = billingService.restorePurchases()


    override fun startConnection() {
        billingService.startConnection()
    }

    override fun endConnection() {
        billingService.endConnection()
    }
}
