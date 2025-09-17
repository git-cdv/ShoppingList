package chkan.ua.shoppinglist.ui.screens.paywall.data

import android.content.Context
import chkan.ua.domain.Logger
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.core.remoteconfigs.RemoteConfigManager
import chkan.ua.shoppinglist.di.ApplicationScope
import chkan.ua.shoppinglist.di.Dispatcher
import chkan.ua.shoppinglist.di.DispatcherType
import com.chkan.billing.domain.usecase.GetSubscriptionsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Currency
import javax.inject.Inject
import javax.inject.Singleton

enum class ProductType { WEEK, MONTH, YEAR }

data class PaywallItem(
    val id: String,
    val type: ProductType,
    val isSelected: Boolean = false,
    val price: String,
    val onlyPrice: String,
    val topName: String,
    val botName: String
)

@Singleton
class PaywallCollector @Inject constructor(
    @ApplicationContext val context: Context,
    @Dispatcher(DispatcherType.IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
    private val logger: Logger,
    private val remoteConfig: RemoteConfigManager,
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase
) {

    companion object {
        const val WEEK_ID = "cl.week.7.99"
        const val MONTH_ID = "cl.month.19.99"
        const val YEAR_ID = "cl.year.59.99"
    }

    private var weeklyPrice: Double = 0.0
    private var monthlyPrice: Double = 0.0
    private var yearlyPrice: Double = 0.0
    private var currency: String = ""
    var isReview: Boolean = true
    private var selectedId: String = ""
    private val currencyFormat = NumberFormat.getCurrencyInstance()

    private val _items = MutableStateFlow<List<PaywallItem>>(emptyList())

    fun init() {
        observeRemoteConfig()
        scope.launch(ioDispatcher) {
            try {
                selectedId = MONTH_ID
                val result = getSubscriptionsUseCase(listOf(WEEK_ID, MONTH_ID, YEAR_ID))
                result
                    .onSuccess {
                        val subscriptions = result.getOrElse { throw Exception("Error getting products") }
                        if (subscriptions.isNotEmpty()) {
                            weeklyPrice = subscriptions[0].price
                            monthlyPrice = subscriptions[1].price
                            yearlyPrice = subscriptions[2].price
                            currency = subscriptions[0].priceCurrencyCode
                            currencyFormat.currency = Currency.getInstance(currency)
                        }
                    }
                    .onFailure { logger.e(it) }
            } catch (e: Exception) {
                logger.e(e,"PaywallCollector init ERROR:$e")
            }
            _items.update { collectPaywallItems() }
        }
    }

    private fun observeRemoteConfig() {
        scope.launch(ioDispatcher) {
            remoteConfig.configState.collect { state ->
                if(state == RemoteConfigManager.ConfigState.Success){
                    isReview = remoteConfig.isLegalEnabled()
                }
            }
        }
    }

    fun getItemsFlow() = _items.asStateFlow()

    fun selectItem(selectedId: String) {
        this.selectedId = selectedId
        val list = _items.value
        val updatedList = list.map { it.copy(isSelected = it.id == selectedId) }
        _items.update { updatedList }
    }

    fun getSelectedId() = selectedId

    private fun collectWeeklyOfMonthPrice(priceMonthly: Double): String {
        return try {
            val resultPrice = priceMonthly / 4
            currencyFormat.format(resultPrice)
        } catch (e: Throwable) {
            logger.e(e)
            priceMonthly.toString()
        }
    }

    private fun collectWeeklyOfYearPrice(priceYearly: Double): String {
        return try {
            val resultPrice = priceYearly / 48
            currencyFormat.format(resultPrice)
        } catch (e: Throwable) {
            logger.e(e)
            priceYearly.toString()
        }
    }

    private fun formatPrice(price: Double): String {
        return try {
            currencyFormat.format(price)
        } catch (e: Throwable) {
            logger.e(e)
            price.toString()
        }
    }

    private fun collectPaywallItems(): List<PaywallItem> {
        return listOf(
            PaywallItem(
                id = WEEK_ID,
                type = ProductType.WEEK,
                isSelected = false,
                price = formatPrice(weeklyPrice),
                onlyPrice = formatPrice(weeklyPrice),
                topName = if (isReview) context.getString(R.string.weekly) else "1",
                botName = if (isReview) context.getString(R.string.plan) else context.getString(R.string.week)
            ),
            PaywallItem(
                id = MONTH_ID,
                type = ProductType.MONTH,
                isSelected = true,
                price = formatPrice(monthlyPrice),
                onlyPrice = collectWeeklyOfMonthPrice(monthlyPrice),
                topName = if (isReview) context.getString(R.string.monthly) else "1",
                botName = if (isReview) context.getString(R.string.plan) else context.getString(R.string.month)
            ),
            PaywallItem(
                id = YEAR_ID,
                type = ProductType.YEAR,
                isSelected = false,
                price = formatPrice(yearlyPrice),
                onlyPrice = collectWeeklyOfYearPrice(yearlyPrice),
                topName = if (isReview) context.getString(R.string.yearly) else "1",
                botName = if (isReview) context.getString(R.string.plan) else context.getString(R.string.year)
            )
        )
    }
}