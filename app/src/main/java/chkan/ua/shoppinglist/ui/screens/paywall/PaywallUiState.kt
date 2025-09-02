package chkan.ua.shoppinglist.ui.screens.paywall

data class PaywallUiState(
    val isReview: Boolean = true,
    val isHardPaywall: Boolean = true,
    val paywallType: String = "",
    val isLoading: Boolean = false,
    val event: PaywallEvent? = null
)

sealed interface PaywallEvent {
    data object ProductPurchased : PaywallEvent
    data object ProductAlreadyPurchasedError : PaywallEvent
    data object RestorePurchasesFailed : PaywallEvent
    data object NetworkError  : PaywallEvent
    data object UnknownError  : PaywallEvent
}