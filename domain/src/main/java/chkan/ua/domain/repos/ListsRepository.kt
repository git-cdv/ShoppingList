package chkan.ua.domain.repos


import chkan.ua.domain.models.ListItems
import chkan.ua.domain.objects.Editable
import kotlinx.coroutines.flow.Flow

interface ListsRepository {
    fun getListsWithItemsFlow(): Flow<List<ListItems>>
    suspend fun addList(title: String, position: Int)
    suspend fun deleteList(listId: Int)
    suspend fun updateTitle(editable: Editable)
    suspend fun getListCount(): Int
    suspend fun moveToTop(id: Int, position: Int)
}