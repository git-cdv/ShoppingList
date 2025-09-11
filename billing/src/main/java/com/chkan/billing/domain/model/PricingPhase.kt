package com.chkan.billing.domain.model

data class PricingPhase(
    val formattedPrice: String,
    val priceCurrencyCode: String,
    val priceAmountMicros: Long,
    val billingPeriod: String,
    val billingCycleCount: Int,
    val recurrenceMode: Int
)

