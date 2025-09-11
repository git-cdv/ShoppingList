package com.chkan.billing.data

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.chkan.billing.data.SubscriptionMapper.toDomainSubscription
import com.chkan.billing.data.SubscriptionMapper.toDomainSubscriptionPurchase
import com.chkan.billing.domain.BillingRepository
import com.chkan.billing.domain.model.Subscription
import com.chkan.billing.domain.model.SubscriptionPurchase
import com.chkan.billing.service.SubscriptionBillingService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BillingRepositoryImpl @Inject constructor(
    private val billingService: SubscriptionBillingService
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
    ): Result<Boolean> {
        // Сначала получаем детали подписки
        val (queryResult, productDetailsList) = billingService.querySubscriptionDetails(listOf(productId))

        if (queryResult.responseCode != BillingClient.BillingResponseCode.OK || productDetailsList.isNullOrEmpty()) {
            val error = billingService.getBillingError(queryResult.responseCode)
            return Result.failure(Exception(error.description))
        }

        val productDetails = productDetailsList.first()

        // Если не передан offerToken, берем первый доступный
        val selectedOfferToken = offerToken
            ?: productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
            ?: return Result.failure(Exception("No offer token available"))

        val billingResult = billingService.launchSubscriptionFlow(
            activity,
            productDetails,
            selectedOfferToken
        )

        return if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Result.success(true)
        } else {
            val error = billingService.getBillingError(billingResult.responseCode)
            Result.failure(Exception(error.description))
        }
    }

    override suspend fun restorePurchases(): Result<List<SubscriptionPurchase>> {
        val (billingResult, purchasesList) = billingService.querySubscriptionPurchases()

        return if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchasesList != null) {
            Result.success(purchasesList.map { it.toDomainSubscriptionPurchase() })
        } else {
            val error = billingService.getBillingError(billingResult.responseCode)
            Result.failure(Exception(error.description))
        }
    }

    override fun startConnection() {
        billingService.startConnection()
    }

    override fun endConnection() {
        billingService.endConnection()
    }
}
