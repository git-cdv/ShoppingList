package chkan.ua.data.models

import androidx.room.ColumnInfo
import androidx.room.Relation
import chkan.ua.domain.models.ListItems

data class ListWithItems(
    val listId: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "position")
    val position: Int,
    @Relation(parentColumn = "listId", entityColumn = "listId")
    val items: List<ItemEntity>,
    @ColumnInfo(name = "is_shared")
    val isShared: Boolean = false)
{
    fun mapToListItem() : ListItems{
        return ListItems(listId, title, position, items.mapToItems(), isShared)
    }
}
