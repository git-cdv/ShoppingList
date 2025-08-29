package chkan.ua.shoppinglist.ui.screens.lists

import chkan.ua.domain.models.ListItemsUi
import chkan.ua.domain.objects.Editable

sealed interface ListUiEvent {
    data class OnEditList(val editable: Editable) : ListUiEvent
    data class OnDeleteList(val listId: String, val isShared: Boolean) : ListUiEvent
    data class OnMoveToTop(val listId: String,val position: Int) : ListUiEvent
    data class OnCardClick(val list : ListItemsUi): ListUiEvent
    data object OnCreateList: ListUiEvent
    data class OnStopSharing(val listId: String) : ListUiEvent
    data class OnStopFollowing(val listId: String) : ListUiEvent
    data class OnShareList(val listId: String) : ListUiEvent
}
