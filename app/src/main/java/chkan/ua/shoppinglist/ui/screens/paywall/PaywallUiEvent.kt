package chkan.ua.shoppinglist.ui.screens.paywall

import android.app.Activity

sealed interface PaywallUiEvent {
    data class Subscribe(val activity: Activity, val role: String) : PaywallUiEvent
    data object SubscribeRestore : PaywallUiEvent
    data class ProductSelected(val id: String): PaywallUiEvent
    data object PaywallEventConsumed : PaywallUiEvent
}