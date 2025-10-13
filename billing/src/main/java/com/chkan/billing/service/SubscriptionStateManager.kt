package com.chkan.billing.service

import android.content.Context
import com.chkan.billing.di.ApplicationScope
import com.chkan.billing.di.Dispatcher
import com.chkan.billing.di.DispatcherType
import com.chkan.billing.domain.BillingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import com.chkan.billing.core.BillingLogger


const val PREFS_NAME = "BasePrefs"
const val IS_SUBSCRIBED_KEY = "is_subscribed"

sealed class SubscriptionState {
    object Loading : SubscriptionState()
    data object Active : SubscriptionState()
    data object Inactive : SubscriptionState()
    data class NewPurchase(val productId: String) : SubscriptionState()
}


@Singleton
class SubscriptionStateManager @Inject constructor(
    private val billingRepository: BillingRepository,
    @ApplicationContext private val context: Context,
    @Dispatcher(DispatcherType.IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
    private val logger: BillingLogger
) {

    private val _subscriptionState = MutableStateFlow<SubscriptionState>(
        SubscriptionState.Loading
    )
    val subscriptionState = _subscriptionState.asStateFlow()

    private val _subscriptionErrorState = MutableStateFlow<Throwable?>(null)
    val subscriptionErrorState = _subscriptionErrorState.asStateFlow()

    init {
        loadCachedState()
        observeBillingUpdate()
    }

    private fun loadCachedState() {
        val sp = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)
        val isSubscribed = sp.getBoolean(IS_SUBSCRIBED_KEY, false)
        _subscriptionState.value = if (isSubscribed) SubscriptionState.Active else SubscriptionState.Inactive
    }

    private fun observeBillingUpdate() {
        scope.launch(ioDispatcher) {
            billingRepository.activeSubscriptionsFlow.collect { result ->
                result
                    .onSuccess { activeSubscriptions ->
                        val newState = if (activeSubscriptions.isNotEmpty()) SubscriptionState.Active else SubscriptionState.Inactive
                        val oldState = _subscriptionState.value

                        if(oldState != newState) {
                            val sp = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)
                            sp.edit { putBoolean(IS_SUBSCRIBED_KEY, activeSubscriptions.isNotEmpty()) }

                            if (oldState != SubscriptionState.Loading && newState == SubscriptionState.Active) {
                                _subscriptionState.value = SubscriptionState.NewPurchase(activeSubscriptions.firstOrNull()?.productId ?: "unknown")
                            } else {
                                _subscriptionState.value = newState
                            }
                        }
                    }
                    .onFailure {
                        _subscriptionErrorState.value = it
                        logger.e(it)
                    }
            }
        }
    }
}
