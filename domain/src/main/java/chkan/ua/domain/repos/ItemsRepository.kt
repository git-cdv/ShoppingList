package chkan.ua.domain.repos

import chkan.ua.domain.models.Item
import chkan.ua.domain.objects.Editable
import kotlinx.coroutines.flow.Flow

interface ItemsRepository {
    fun getListWithItemsFlowById(listId: Int): Flow<List<Item>>
    suspend fun addItem(item: Item)
    suspend fun deleteItem(itemId: Int)
    suspend fun updateContent(editable: Editable)
    suspend fun clearReadyItems(listId: Int)
    suspend fun markItemReady(itemId: Int, state: Boolean)
    suspend fun moveToTop(id: Int, position: Int)
}