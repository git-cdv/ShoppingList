package chkan.ua.data.sources

import chkan.ua.data.models.ListEntity
import chkan.ua.data.models.ListWithItems
import kotlinx.coroutines.flow.Flow

interface DataSource {
    fun getListsWithItemsFlow(): Flow<List<ListWithItems>>
    suspend fun addList(list: ListEntity)
    suspend fun deleteList(listId: Int)
}