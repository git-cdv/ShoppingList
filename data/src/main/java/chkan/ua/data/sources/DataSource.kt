package chkan.ua.data.sources

import chkan.ua.data.models.ItemEntity
import chkan.ua.data.models.ListEntity
import chkan.ua.data.models.ListWithItems
import chkan.ua.domain.models.Item
import chkan.ua.domain.models.ListItems
import chkan.ua.domain.objects.Editable
import kotlinx.coroutines.flow.Flow

interface DataSource {
    fun getListsWithItemsFlow(): Flow<List<ListWithItems>>
    fun getItemsFlowByListId(listId: String): Flow<List<ItemEntity>>
    suspend fun addList(list: ListEntity)
    suspend fun deleteList(listId: String)
    suspend fun updateTitle(editable: Editable)
    suspend fun getListCount(): Int
    suspend fun getMaxListPosition(): Int?
    suspend fun getMaxItemPosition(): Int?
    suspend fun addItem(item: ItemEntity)
    suspend fun deleteItem(itemId: String)
    suspend fun markItemReady(itemId: String, state: Boolean)
    suspend fun clearReadyItems(listId: String)
    suspend fun updateContent(editable: Editable)
    suspend fun moveToTop(id: String, position: Int)
    suspend fun moveItemToTop(id: String, position: Int)
    suspend fun getListWithItemsById(listId: String): ListWithItems?
    suspend fun deleteItemsOfList(listId: String)
    suspend fun addItems(items: List<Item>)
}