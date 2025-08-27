package chkan.ua.domain.repos

import chkan.ua.domain.models.Item
import chkan.ua.domain.models.ListItems
import kotlinx.coroutines.flow.Flow

interface RemoteRepository {
    suspend fun createSharedList(userId:String, list: ListItems): String
    fun getListWithItemsFlowById(listId:String): Flow<List<Item>>

    suspend fun markItemReady(listId: String, itemId: String, isReady: Boolean)
    suspend fun addItem(item: Item)
    suspend fun deleteItem(listId: String, itemId: String, wasReady: Boolean)
    suspend fun clearReadyItems(listId: String)
}