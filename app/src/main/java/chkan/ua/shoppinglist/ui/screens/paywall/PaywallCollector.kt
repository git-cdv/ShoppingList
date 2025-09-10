package chkan.ua.shoppinglist.ui.screens.paywall

import android.content.Context
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.di.ApplicationScope
import chkan.ua.shoppinglist.di.Dispatcher
import chkan.ua.shoppinglist.di.DispatcherType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
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
    //private val sdkPurchases: Purchases,
    //private val sdkConfig: RemoteConfig,
    @Dispatcher(DispatcherType.IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) {

    companion object {
        const val WEEK_ID = "pt.week.7.99"
        const val MONTH_ID = "pt.month.19.99"
        const val YEAR_ID = "pt.year.59.99"
    }

    private var weeklyPrice: Double = 0.0
    private var monthlyPrice: Double = 0.0
    private var yearlyPrice: Double = 0.0
    private var currency: String = ""
    private var isReview: Boolean = true
    private var selectedId: String = ""
    private val currencyFormat = NumberFormat.getCurrencyInstance()

    private val _items = MutableStateFlow<List<PaywallItem>>(emptyList())

    fun init() {
        scope.launch(ioDispatcher) {
            try {
                /*isReview = sdkConfig.isSubscriptionStyleFull()
                paywallType = sdkConfig.getActivePaywallName()
                isV3 = paywallType == V3_NAME && isReview == false
                selectedId = if (isV3) WEEK_ID else MONTH_ID
                val products = sdkPurchases.getProducts(listOf(WEEK_ID, MONTH_ID, YEAR_ID))
                weeklyPrice = products[0].price.amount
                monthlyPrice = products[1].price.amount
                yearlyPrice = products[2].price.amount
                currency = products[0].price.currencyCode*/
                currencyFormat.currency = Currency.getInstance(currency)
            } catch (e: Exception) {
                Timber.e(e)
                Timber.tag("PAYWALL").d("PaywallCollector init ERROR:$e")
            }
            _items.update { collectPaywallItems() }
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
            Timber.e(e)
            priceMonthly.toString()
        }
    }

    private fun collectWeeklyOfYearPrice(priceYearly: Double): String {
        return try {
            val resultPrice = priceYearly / 48
            currencyFormat.format(resultPrice)
        } catch (e: Throwable) {
            Timber.e(e)
            priceYearly.toString()
        }
    }

    private fun formatPrice(price: Double): String {
        return try {
            currencyFormat.format(price)
        } catch (e: Throwable) {
            Timber.e(e)
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