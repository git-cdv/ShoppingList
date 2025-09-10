package chkan.ua.shoppinglist.ui.screens.paywall

sealed interface PaywallUiEvent {
    data object Subscribe : PaywallUiEvent
    data object SubscribeRestore : PaywallUiEvent
    data class ProductSelected(val id: String): PaywallUiEvent
}