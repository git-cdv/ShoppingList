package com.chkan.billing.domain.model

data class Subscription(
    val productId: String,
    val title: String,
    val description: String,
    val offers: List<SubscriptionOffer>
)