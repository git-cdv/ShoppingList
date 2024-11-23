package chkan.ua.data.sources

import chkan.ua.data.models.ItemEntity
import chkan.ua.data.models.ListEntity
import chkan.ua.data.models.ListWithItems
import kotlinx.coroutines.flow.Flow

interface DataSource {
    fun getListsWithItemsFlow(): Flow<List<ListWithItems>>
    fun getItemsFlowByListId(listId: Int): Flow<List<ItemEntity>>
    suspend fun addList(list: ListEntity)
    suspend fun deleteList(listId: Int)
    suspend fun getListCount(): Int
    suspend fun addItem(item: ItemEntity)
    suspend fun deleteItem(itemId: Int)
    suspend fun markItemReady(itemId: Int, state: Boolean)
    suspend fun clearReadyItems(listId: Int)
}