package chkan.ua.shoppinglist.ui.screens.items

import chkan.ua.domain.models.Item

data class ItemsState(
    val listId: String = "",
    val isEmpty: Boolean = false,
    val notReadyItems : List<Item> = listOf(),
    val readyItems : List<Item> = listOf(),
    val isShared: Boolean = false,
)
