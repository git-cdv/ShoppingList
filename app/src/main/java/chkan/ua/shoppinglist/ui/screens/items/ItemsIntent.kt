package chkan.ua.shoppinglist.ui.screens.items

import chkan.ua.domain.models.Item
import chkan.ua.domain.objects.Editable

sealed interface ItemsIntent {
    data class AddItem(val title: String, val note: String? = null) : ItemsIntent
    data class DeleteItem(val item: Item) : ItemsIntent
    data class EditItem(val editable: Editable) : ItemsIntent
    data class MarkReady(val item: Item, val state: Boolean) : ItemsIntent
    data class MoveToTop(val id: String, val position: Int) : ItemsIntent
    data class ClearReadyItems(val listId: String) : ItemsIntent
    data class ShareList(val listId: String) : ItemsIntent
}