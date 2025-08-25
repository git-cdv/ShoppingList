package chkan.ua.data.sources.room

import chkan.ua.data.models.ItemEntity
import chkan.ua.data.models.ListEntity
import chkan.ua.data.models.ListWithItems
import chkan.ua.data.sources.DataSource
import chkan.ua.data.sources.HistoryDataSource
import chkan.ua.domain.models.ListItems
import chkan.ua.domain.objects.Editable
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomSourceImpl @Inject constructor (
    private val listsDao: ListsDao,
    private val itemsDao: ItemsDao,
    private val historyDao: HistoryItemDao
) : DataSource, HistoryDataSource {

    override fun getListsWithItemsFlow(): Flow<List<ListWithItems>> = listsDao.getListsWithItemsFlow()

    override fun getItemsFlowByListId(listId: Int): Flow<List<ItemEntity>> = itemsDao.getItemsFlowByListId(listId)

    override suspend fun addList(list: ListEntity) {
        listsDao.addList(list)
    }

    override suspend fun deleteList(listId: Int) {
        listsDao.deleteListById(listId)
    }

    override suspend fun updateTitle(editable: Editable) {
        listsDao.updateTitle(editable.id, editable.title)
    }

    override suspend fun getListCount() = listsDao.getListCount()
    override suspend fun getMaxListPosition() = listsDao.getMaxListPosition()
    override suspend fun getMaxItemPosition() = itemsDao.getMaxItemPosition()

    override suspend fun moveToTop(id: Int, position: Int) {
        listsDao.moveToTop(id,position)
    }

    override suspend fun addItem(item: ItemEntity) {
        itemsDao.addItem(item)
    }

    override suspend fun deleteItem(itemId: Int) {
        itemsDao.deleteById(itemId)
    }

    override suspend fun moveItemToTop(id: Int, position: Int) {
        itemsDao.moveToTop(id,position)
    }

    override suspend fun getListWithItemsById(listId: Int): ListWithItems? {
        return listsDao.getListWithItemsById(listId)
    }

    override suspend fun markAsShared(listId: Int, firestoreId: String) {
        listsDao.markAsShared(listId, firestoreId)
    }

    override suspend fun markItemReady(itemId: Int, state: Boolean) {
        val stateAsInt = if (state) 1 else 0
        itemsDao.markItemReady(itemId,stateAsInt)
    }

    override suspend fun clearReadyItems(listId: Int) {
        itemsDao.clearReadyItems(listId)
    }
    override suspend fun updateContent(editable: Editable) {
        itemsDao.updateContent(editable.id, editable.title, note = editable.note)
    }

    override fun getHistory() = historyDao.getHistory()

    override suspend fun incrementOrInsertInHistory(name: String) {
        historyDao.incrementOrInsertInHistory(name)
    }
}