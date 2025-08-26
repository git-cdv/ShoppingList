package chkan.ua.data.sources

import chkan.ua.domain.models.Item
import chkan.ua.domain.models.ListItems
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    suspend fun createSharedList(userId:String, list: ListItems): String
    fun getListWithItemsFlowById(listId: String): Flow<List<Item>>
}