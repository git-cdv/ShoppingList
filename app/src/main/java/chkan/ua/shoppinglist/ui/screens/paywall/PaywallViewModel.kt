package chkan.ua.shoppinglist.ui.screens.paywall

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chkan.ua.shoppinglist.core.analytics.Analytics
import chkan.ua.shoppinglist.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PaywallViewModel @Inject constructor(
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val paywallCollector: PaywallCollector,
/*    private val sdkPurchases: Purchases,
    private val subscribeHandler: SubscribeHandler,
    private val analytics: Analytics,
    private val errorMapper: ErrorMapper,
    private val config: RemoteConfig*/
) : ViewModel() {

    private val _paywallUiState = MutableStateFlow(PaywallUiState())
    val paywallUiState = _paywallUiState.asStateFlow()

  /*  init {
        _paywallUiState.update {
            it.copy(
                isReview = config.isSubscriptionStyleFull()
            )
        }
    }*/

    private var purchasesJob: Job? = null
    private var restoreJob: Job? = null

    val paywallItemsFlow = paywallCollector.getItemsFlow()

    fun selectPaywallItem(selectedId: String){
        paywallCollector.selectItem(selectedId)
    }

   /* fun onSubscribe(activity: Activity) {
        val productId = paywallCollector.getSelectedId()
        analytics.logEvent(PaywallAnalyticsEvent.SubscriptionSubscribeClicked(config.getActivePaywallName(),productId))

        if (purchasesJob?.isActive == true) {
            return
        }
        purchasesJob = applicationScope.launch(Dispatchers.IO) {
            _paywallUiState.update { it.copy(isLoading = true) }
            try{
                sdkPurchases.purchase(activity,productId)
                setIsSubscribed()
                _paywallUiState.update {
                    it.copy(
                        isLoading = false,
                        event = PaywallEvent.ProductPurchased
                    )
                }
                analytics.logEvent(PaywallAnalyticsEvent.SubscriptionPurchased(config.getActivePaywallName(),productId))
            } catch (e: Throwable){
                Timber.e(e)
                if (e is PurchasesException){
                    _paywallUiState.update {
                        it.copy(
                            isLoading = false,
                            event = collectPurchasesErrorEvent(e)
                        )
                    }
                    analytics.logEvent(PaywallAnalyticsEvent.SubscriptionError(config.getActivePaywallName(),productId,e.error.name))
                } else {
                    analytics.logEvent(PaywallAnalyticsEvent.SubscriptionError(config.getActivePaywallName(),productId,errorMapper.map(e).short()))
                }
            }
        }
    }

    fun setIsSubscribed() {
        subscribeHandler.setIsSubscribed()
    }

    private fun collectPurchasesErrorEvent(e: PurchasesException): PaywallEvent? {
        if (e.error == PurchasesError.PurchaseCancelledError) return null
        if (e.error == PurchasesError.ProductAlreadyPurchasedError){
            subscribeHandler.setIsSubscribed()
            return PaywallEvent.ProductAlreadyPurchasedError
        }
        if (e.error == PurchasesError.NetworkError){
            return PaywallEvent.NetworkError
        }
        return PaywallEvent.UnknownError
    }

    fun consumeEvent() {
        _paywallUiState.update { it.copy(event = null) }
    }

    fun onSubscribeRestore() {
        if (restoreJob?.isActive == true) {
            return
        }
        restoreJob = viewModelScope.launch {
            val isSubscribed = subscribeHandler.restore()
            if (isSubscribed == true){
                setIsSubscribed()
                _paywallUiState.update {
                    it.copy(
                        isLoading = false,
                        event = PaywallEvent.ProductPurchased
                    )
                }
            } else {
                _paywallUiState.update {
                    it.copy(
                        isLoading = false,
                        event = PaywallEvent.RestorePurchasesFailed
                    )
                }
            }
        }
    }*/
}