package com.chkan.billing.domain.usecase

import com.chkan.billing.domain.BillingRepository
import com.chkan.billing.domain.model.SubscriptionPurchase
import javax.inject.Inject

class RestorePurchaseUseCase @Inject constructor(
    private val billingRepository: BillingRepository
) {

    suspend fun restore(): Result<List<SubscriptionPurchase>> {
        return billingRepository.restorePurchases()
    }
}