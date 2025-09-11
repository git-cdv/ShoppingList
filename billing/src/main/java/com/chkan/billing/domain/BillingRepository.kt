package com.chkan.billing.domain

import android.app.Activity
import com.chkan.billing.domain.model.Subscription
import com.chkan.billing.domain.model.SubscriptionPurchase
import kotlinx.coroutines.flow.Flow


interface BillingRepository {
    val activeSubscriptionsFlow: Flow<Result<List<SubscriptionPurchase>>>
    val billingConnectionState: Flow<Result<Boolean>>

    suspend fun querySubscriptions(productIds: List<String>): Result<List<Subscription>>
    suspend fun launchSubscriptionPurchase(
        activity: Activity,
        productId: String,
        offerToken: String? = null
    ): Result<Boolean>
    suspend fun acknowledgePurchase(purchaseToken: String): Result<Boolean>
    suspend fun queryActivePurchases(): Result<List<SubscriptionPurchase>>
    suspend fun restorePurchases(): Result<List<SubscriptionPurchase>>
    fun startConnection()
    fun endConnection()
}
