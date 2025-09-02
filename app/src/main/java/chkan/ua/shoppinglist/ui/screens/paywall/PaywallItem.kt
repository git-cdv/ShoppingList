package chkan.ua.shoppinglist.ui.screens.paywall

enum class PaywallType { WEEK, MONTH, YEAR }

data class PaywallItem(
    val id: String,
    val type: PaywallType,
    val isSelected: Boolean = false,
    val price: String,
    val onlyPrice: String,
    val topName: String,
    val botName: String,
    val clearTopType: PaywallType = PaywallType.WEEK
)
