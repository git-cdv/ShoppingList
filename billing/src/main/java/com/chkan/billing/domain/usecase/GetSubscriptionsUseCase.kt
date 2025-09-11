package com.chkan.billing.domain.usecase

import com.chkan.billing.domain.BillingRepository
import com.chkan.billing.domain.model.Subscription
import javax.inject.Inject

class GetSubscriptionsUseCase @Inject constructor(
    private val billingRepository: BillingRepository
) {
    suspend fun getSubscriptions(productIds: List<String>): Result<List<Subscription>> {
        return billingRepository.querySubscriptions(productIds)
    }
}
