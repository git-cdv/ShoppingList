package chkan.ua.domain.repos


import chkan.ua.domain.models.ListItems
import chkan.ua.domain.objects.Editable
import kotlinx.coroutines.flow.Flow

interface ListsRepository {
    fun getListsWithItemsFlow(): Flow<List<ListItems>>
    suspend fun addList(title: String)
    suspend fun deleteList(listId: String)
    suspend fun updateTitle(editable: Editable)
    suspend fun getListCount(): Int
    suspend fun moveToTop(listId: String, position: Int)
    suspend fun getListWithItemsById(listId: String): ListItems?
}