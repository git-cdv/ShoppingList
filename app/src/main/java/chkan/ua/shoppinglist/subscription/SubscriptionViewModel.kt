package chkan.ua.shoppinglist.subscription

import androidx.lifecycle.ViewModel
import com.chkan.billing.domain.BillingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val billingRepository: BillingRepository
) : ViewModel() {


}
