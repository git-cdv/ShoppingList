package chkan.ua.shoppinglist.ui.screens.items

import chkan.ua.domain.models.Item

data class ItemsState(
    val isEmpty: Boolean = false,
    val notReadyItems : List<Item> = listOf(),
    val readyItems : List<Item> = listOf()
)
