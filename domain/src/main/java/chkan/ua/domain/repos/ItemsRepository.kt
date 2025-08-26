package chkan.ua.domain.repos

import chkan.ua.domain.models.Item
import chkan.ua.domain.objects.Editable
import kotlinx.coroutines.flow.Flow

interface ItemsRepository {
    fun getListWithItemsFlowById(listId: String): Flow<List<Item>>
    suspend fun addItem(item: Item)
    suspend fun deleteItem(itemId: String)
    suspend fun updateContent(editable: Editable)
    suspend fun clearReadyItems(listId: String)
    suspend fun markItemReady(itemId: String, state: Boolean)
    suspend fun moveToTop(itemId: String, position: Int)
    suspend fun deleteItemsOfList(listId: String)
}