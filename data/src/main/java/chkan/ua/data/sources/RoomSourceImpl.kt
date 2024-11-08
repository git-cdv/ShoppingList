package chkan.ua.data.sources

import chkan.ua.data.models.ListEntity
import chkan.ua.data.models.ListWithItems
import chkan.ua.data.sources.room.ItemsDao
import chkan.ua.data.sources.room.ListsDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomSourceImpl @Inject constructor (
    private val listsDao: ListsDao,
    private val itemsDao: ItemsDao,
) : DataSource {

    override fun getListsWithItemsFlow(): Flow<List<ListWithItems>> = listsDao.getListsWithItemsFlow()

    override suspend fun addList(list: ListEntity) {
        listsDao.addList(list)
    }

    override suspend fun deleteList(listId: Int) {
        listsDao.deleteListById(listId)
    }
}