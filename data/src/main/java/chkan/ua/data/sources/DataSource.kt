package chkan.ua.data.sources

import chkan.ua.data.models.ItemEntity
import chkan.ua.data.models.ListEntity
import chkan.ua.data.models.ListWithItems
import chkan.ua.domain.models.ListItems
import chkan.ua.domain.objects.Editable
import kotlinx.coroutines.flow.Flow

interface DataSource {
    fun getListsWithItemsFlow(): Flow<List<ListWithItems>>
    fun getItemsFlowByListId(listId: Int): Flow<List<ItemEntity>>
    suspend fun addList(list: ListEntity)
    suspend fun deleteList(listId: Int)
    suspend fun updateTitle(editable: Editable)
    suspend fun getListCount(): Int
    suspend fun getMaxListPosition(): Int?
    suspend fun getMaxItemPosition(): Int?
    suspend fun addItem(item: ItemEntity)
    suspend fun deleteItem(itemId: Int)
    suspend fun markItemReady(itemId: Int, state: Boolean)
    suspend fun clearReadyItems(listId: Int)
    suspend fun updateContent(editable: Editable)
    suspend fun moveToTop(id: Int, position: Int)
    suspend fun moveItemToTop(id: Int, position: Int)
    suspend fun getListWithItemsById(listId: Int): ListWithItems?
    suspend fun markAsShared(listId: Int, firestoreId: String)
}