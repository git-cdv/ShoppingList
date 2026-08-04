package chkan.ua.shoppinglist.ui.screens.items

import androidx.compose.runtime.Stable
import chkan.ua.core.models.ListRole
import chkan.ua.domain.models.Item

@Stable
data class ItemsState(
    val listId: String = "",
    val isEmpty: Boolean = false,
    val notReadyItems : List<Item> = listOf(),
    val readyItems : List<Item> = listOf(),
    val role: ListRole = ListRole.LOCAL,
)
