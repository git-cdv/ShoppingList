package com.chkan.billing.data

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.chkan.billing.domain.model.PurchaseState
import com.chkan.billing.domain.model.Subscription
import com.chkan.billing.domain.model.SubscriptionPurchase

object SubscriptionMapper {

    fun ProductDetails.toDomainSubscription(): Subscription {
        val subscriptionOffer = subscriptionOfferDetails?.firstOrNull()
        val pricingPhase = subscriptionOffer?.pricingPhases?.pricingPhaseList?.firstOrNull()

        return Subscription(
            productId = productId,
            priceCurrencyCode = pricingPhase?.priceCurrencyCode ?: "",
            price = (pricingPhase?.priceAmountMicros ?: 0L) / 1_000_000.0
        )
    }

    fun Purchase.toDomainSubscriptionPurchase(): SubscriptionPurchase {
        return SubscriptionPurchase(
            orderId = orderId ?: "",
            packageName = packageName,
            productId = products.firstOrNull() ?: "",
            purchaseTime = purchaseTime,
            purchaseState = when (purchaseState) {
                Purchase.PurchaseState.PURCHASED -> PurchaseState.PURCHASED
                Purchase.PurchaseState.PENDING -> PurchaseState.PENDING
                else -> PurchaseState.UNSPECIFIED
            },
            purchaseToken = purchaseToken,
            isAcknowledged = isAcknowledged,
            isAutoRenewing = isAutoRenewing,
            quantity = quantity
        )
    }
}
