package com.chkan.billing.domain.usecase

import android.app.Activity
import com.chkan.billing.domain.BillingRepository
import javax.inject.Inject

class SubscriptionPurchaseUseCase @Inject constructor(
    private val billingRepository: BillingRepository
) {
    suspend fun purchase(
        activity: Activity,
        productId: String,
        offerToken: String? = null
    ) {
        billingRepository.launchSubscriptionPurchase(activity, productId, offerToken)
    }
}
