package com.chkan.billing.domain.usecase

import android.app.Activity
import com.chkan.billing.domain.BillingRepository
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
}
