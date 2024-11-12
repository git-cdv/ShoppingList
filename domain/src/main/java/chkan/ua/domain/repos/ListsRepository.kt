package chkan.ua.domain.repos


import chkan.ua.domain.models.Item
import chkan.ua.domain.models.ListItems
import kotlinx.coroutines.flow.Flow

interface ListsRepository {
    fun getListsWithItemsFlow(): Flow<List<ListItems>>
    fun getListWithItemsFlowById(listId: Int): Flow<List<Item>>
    suspend fun addList(title: String, position: Int)
    suspend fun deleteList(listId: Int)
    suspend fun getListCount(): Int
}