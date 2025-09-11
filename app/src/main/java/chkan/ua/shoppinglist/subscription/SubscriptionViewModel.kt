package chkan.ua.shoppinglist.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chkan.billing.domain.BillingRepository
import com.chkan.billing.domain.usecase.SubscriptionPurchaseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionPurchaseUseCase: SubscriptionPurchaseUseCase,
    private val billingRepository: BillingRepository
) : ViewModel() {

    init {
        billingRepository.startConnection()

        viewModelScope.launch {
            billingRepository.activeSubscriptionsFlow.collect { purchases ->
                handlePurchases(purchases)
            }
        }
    }

}
