package com.chkan.billing.domain.model

data class SubscriptionOffer(
    val basePlanId: String,
    val offerId: String?,
    val offerToken: String,
    val pricingPhases: List<PricingPhase>,
    val tags: List<String>
)

