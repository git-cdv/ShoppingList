package com.chkan.billing.domain.model

data class SubscriptionPurchase(
    val orderId: String,
    val packageName: String,
    val productId: String,
    val purchaseTime: Long,
    val purchaseState: PurchaseState,
    val purchaseToken: String,
    val isAcknowledged: Boolean,
    val isAutoRenewing: Boolean,
    val quantity: Int = 1
)

enum class PurchaseState {
    UNSPECIFIED,
    PURCHASED,
    PENDING
}

