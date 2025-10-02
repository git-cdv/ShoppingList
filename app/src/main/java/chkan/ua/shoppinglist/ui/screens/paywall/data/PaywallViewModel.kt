package chkan.ua.shoppinglist.ui.screens.paywall.data

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chkan.ua.core.exceptions.ResourceCode
import chkan.ua.core.exceptions.UserMessageException
import chkan.ua.shoppinglist.core.services.ErrorHandler
import chkan.ua.shoppinglist.di.ApplicationScope
import chkan.ua.shoppinglist.ui.screens.paywall.PaywallEvent
import chkan.ua.shoppinglist.ui.screens.paywall.PaywallUiEvent
import chkan.ua.shoppinglist.ui.screens.paywall.PaywallUiState
import com.chkan.billing.domain.error.PurchasesError
import com.chkan.billing.domain.error.PurchasesException
import com.chkan.billing.domain.usecase.RestorePurchaseUseCase
import com.chkan.billing.domain.usecase.SubscriptionPurchaseUseCase
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
    private val purchaseUseCase: SubscriptionPurchaseUseCase,
    private val restoreUseCase: RestorePurchaseUseCase,
    private val errorHandler: ErrorHandler,
) : ViewModel() {

    private val _paywallUiState = MutableStateFlow(PaywallUiState())
    val paywallUiState = _paywallUiState.asStateFlow()

    private var purchasesJob: Job? = null
    private var restoreJob: Job? = null

    val paywallItemsFlow = paywallCollector.getItemsFlow()

    fun isReview() = paywallCollector.isReview
    fun onUiEvent(event: PaywallUiEvent) {
        when (event) {
            is PaywallUiEvent.ProductSelected -> paywallCollector.selectItem(event.id)
            is PaywallUiEvent.Subscribe -> onSubscribe(event.activity)
            PaywallUiEvent.SubscribeRestore -> onSubscribeRestore()
            PaywallUiEvent.PaywallEventConsumed -> consumeEvent()
        }
    }

    fun onSubscribe(activity: Activity) {
        val productId = paywallCollector.getSelectedId()
        //analytics.logEvent(PaywallAnalyticsEvent.SubscriptionSubscribeClicked(config.getActivePaywallName(),productId))

        if (purchasesJob?.isActive == true) {
            return
        }
        purchasesJob = applicationScope.launch(Dispatchers.IO) {
            _paywallUiState.update { it.copy(isLoading = true) }
            try {
                purchaseUseCase.purchase(activity, productId)
                //analytics.logEvent(PaywallAnalyticsEvent.SubscriptionPurchased(config.getActivePaywallName(),productId))
            } catch (e: Throwable) {
                Timber.e(e)
                if (e is PurchasesException) {
                    handleError(e)
                    _paywallUiState.update {
                        it.copy(isLoading = false)
                    }
                    //analytics.logEvent(PaywallAnalyticsEvent.SubscriptionError(config.getActivePaywallName(),productId,e.error.name))
                } else {
                    //analytics.logEvent(PaywallAnalyticsEvent.SubscriptionError(config.getActivePaywallName(),productId,errorMapper.map(e).short()))
                }
            }
        }
    }

    private fun handleError(e: PurchasesException) {
        if (e.error == PurchasesError.ProductAlreadyPurchasedError) {
            onSubscribeRestore()
        }
        if (e.error == PurchasesError.NetworkError) {
            errorHandler.handle(UserMessageException(ResourceCode.NO_INTERNET_CONNECTION))
        }
        if (e.error != PurchasesError.PurchaseCancelledError){
            errorHandler.handle(UserMessageException(ResourceCode.UNKNOWN_ERROR))
        }
    }

    fun consumeEvent() {
        _paywallUiState.update { it.copy(event = null) }
    }

    fun onSubscribeRestore() {
        if (restoreJob?.isActive == true) {
            return
        }
        restoreJob = viewModelScope.launch {
            restoreUseCase()
                .onSuccess {
                    _paywallUiState.update { it.copy(isLoading = false) }
                }
                .onFailure {
                    _paywallUiState.update {
                        it.copy(
                            isLoading = false,
                            event = PaywallEvent.RestorePurchasesFailed
                        )
                    }
                }
        }
    }
}