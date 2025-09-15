package com.chkan.billing.domain.model

data class Subscription(
    val productId: String,
    val priceCurrencyCode: String,
    val price: Double,
)