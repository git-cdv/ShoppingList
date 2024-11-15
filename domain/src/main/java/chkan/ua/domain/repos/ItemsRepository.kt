package chkan.ua.domain.repos

import chkan.ua.domain.models.Item
import kotlinx.coroutines.flow.Flow

interface ItemsRepository {
    fun getListWithItemsFlowById(listId: Int): Flow<List<Item>>
    fun getReadyItemsFlowByListId(listId: Int): Flow<List<Item>>
    suspend fun addItem(item: Item)
    suspend fun deleteItem(itemId: Int)
    suspend fun markItemReady(itemId: Int, state: Boolean)
}