package chkan.ua.shoppinglist.ui.screens.paywall

data class PaywallUiState(
    val isLoading: Boolean = false,
    val event: PaywallEvent? = null,
    val paywallType: String = "",
)

sealed interface PaywallEvent {
    data object RestorePurchasesFailed : PaywallEvent
}