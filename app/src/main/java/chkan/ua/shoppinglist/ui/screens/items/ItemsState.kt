package chkan.ua.shoppinglist.ui.screens.items

import chkan.ua.shoppinglist.core.components.Component

data class ItemsState(
    val isEmpty: Boolean = false,
    val historyComponent: Component? = null
)
