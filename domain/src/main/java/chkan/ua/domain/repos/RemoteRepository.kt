package chkan.ua.domain.repos

import chkan.ua.domain.models.Item
import chkan.ua.domain.models.ListItems
import kotlinx.coroutines.flow.Flow

interface RemoteRepository {
    suspend fun createSharedList(userId:String, list: ListItems): String
    fun getListWithItemsFlowById(listId:String): Flow<List<Item>>
}