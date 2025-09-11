package com.chkan.billing.data

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.chkan.billing.domain.model.PurchaseState
import com.chkan.billing.domain.model.Subscription
import com.chkan.billing.domain.model.SubscriptionOffer
import com.chkan.billing.domain.model.SubscriptionPurchase

object SubscriptionMapper {

    fun ProductDetails.toDomainSubscription(): Subscription {
        return Subscription(
            productId = productId,
            title = title,
            description = description,
            offers = subscriptionOfferDetails?.map { offer ->
                SubscriptionOffer(
                    basePlanId = offer.basePlanId,
                    offerId = offer.offerId,
                    offerToken = offer.offerToken,
                    pricingPhases = offer.pricingPhases.pricingPhaseList.map { phase ->
                        com.chkan.billing.domain.model.PricingPhase(
                            formattedPrice = phase.formattedPrice,
                            priceCurrencyCode = phase.priceCurrencyCode,
                            priceAmountMicros = phase.priceAmountMicros,
                            billingPeriod = phase.billingPeriod,
                            billingCycleCount = phase.billingCycleCount,
                            recurrenceMode = phase.recurrenceMode
                        )
                    },
                    tags = offer.offerTags
                )
            } ?: emptyList()
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
