package chkan.ua.shoppinglist.components.history_list

import androidx.compose.runtime.Immutable
import chkan.ua.domain.models.HistoryItem

@Immutable
data class HistoryComponentState(
    val isShow: Boolean = false,
    val isShort: Boolean = false,
    val list: List<HistoryItem>
)