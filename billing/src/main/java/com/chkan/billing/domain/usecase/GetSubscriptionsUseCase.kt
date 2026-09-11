package com.chkan.billing.domain.usecase

import com.chkan.billing.domain.BillingRepository
import com.chkan.billing.domain.model.ProductType
import com.chkan.billing.domain.model.Subscription
import javax.inject.Inject

class GetSubscriptionsUseCase @Inject constructor(
    private val billingRepository: BillingRepository
) {
    suspend operator fun invoke(
        productIds: List<String>,
        productType: ProductType = ProductType.SUBS
    ): Result<List<Subscription>> {
        return billingRepository.querySubscriptions(productIds, productType)
    }
}
