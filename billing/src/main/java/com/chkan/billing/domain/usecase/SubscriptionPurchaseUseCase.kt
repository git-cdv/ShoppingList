package com.chkan.billing.domain.usecase

import android.app.Activity
import com.chkan.billing.domain.BillingRepository
import com.chkan.billing.domain.model.SubscriptionPurchase
import javax.inject.Inject

class SubscriptionPurchaseUseCase @Inject constructor(
    private val billingRepository: BillingRepository
) {
    suspend fun purchaseSubscription(
        activity: Activity,
        productId: String,
        offerToken: String? = null
    ): Result<Boolean> {
        return billingRepository.launchSubscriptionPurchase(activity, productId, offerToken)
    }

    suspend fun acknowledgePurchase(purchaseToken: String): Result<Boolean> {
        return billingRepository.acknowledgePurchase(purchaseToken)
    }

    suspend fun restorePurchases(): Result<List<SubscriptionPurchase>> {
        return billingRepository.restorePurchases()
    }
}
