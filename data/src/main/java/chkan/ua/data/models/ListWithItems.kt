package chkan.ua.data.models

import chkan.ua.domain.models.ListItems

data class ListWithItems(val list: ListEntity, val items: List<ItemEntity>) {

    fun mapToListItem() : ListItems{
        return ListItems(id = list.id, title = list.title, position = list.position, items = items.mapToItems())
    }
}
