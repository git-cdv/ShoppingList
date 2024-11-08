package chkan.ua.domain.repos

interface ListsRepository {
    fun getListsWithItemsFlow(): Flow<List<ListWithItems>>
    suspend fun addList(list: ListEntity)
    suspend fun deleteList(listId: Int)
}