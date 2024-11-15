package chkan.ua.data.sources

import chkan.ua.data.models.ItemEntity
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

    override fun getItemsFlowByListId(listId: Int): Flow<List<ItemEntity>> = itemsDao.getItemsFlowByListId(listId)

    override fun getReadyItemsFlowByListId(listId: Int): Flow<List<ItemEntity>> = itemsDao.getReadyItemsFlowByListId(listId)

    override suspend fun addList(list: ListEntity) {
        listsDao.addList(list)
    }

    override suspend fun deleteList(listId: Int) {
        listsDao.deleteListById(listId)
    }

    override suspend fun getListCount() = listsDao.getListCount()

    override suspend fun addItem(item: ItemEntity) {
        itemsDao.addItem(item)
    }

    override suspend fun deleteItem(itemId: Int) {
        itemsDao.deleteById(itemId)
    }

    override suspend fun markItemReady(itemId: Int, state: Boolean) {
        val stateAsInt = if (state) 1 else 0
        itemsDao.markItemReady(itemId,stateAsInt)
    }
}