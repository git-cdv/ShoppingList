package chkan.ua.data.sources

import chkan.ua.domain.models.Item
import chkan.ua.domain.models.ListItems
import chkan.ua.domain.models.ListSummary
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    suspend fun createSharedList(userId:String, list: ListItems): String
    fun getListWithItemsFlowById(listId: String): Flow<List<Item>>
    fun getAllListsSummaryFlow(userId: String): Flow<List<ListSummary>>
    suspend fun markItemReady(listId: String, itemId: String, isReady: Boolean)
    suspend fun addItem(item: Item)
    suspend fun deleteItem(listId: String, itemId: String, wasReady: Boolean)
    suspend fun clearReadyItems(listId: String)
}