package com.chkan.billing.domain.usecase

import android.app.Activity
import com.chkan.billing.domain.BillingRepository
import com.chkan.billing.domain.model.ProductType
import javax.inject.Inject

class SubscriptionPurchaseUseCase @Inject constructor(
    private val billingRepository: BillingRepository
) {
    suspend fun purchase(
        activity: Activity,
        productId: String,
        productType: ProductType = ProductType.SUBS,
        offerToken: String? = null
    ) {
        billingRepository.launchSubscriptionPurchase(activity, productId, productType, offerToken)
    }
}
