package chkan.ua.data.sources.room

import chkan.ua.data.models.ItemEntity
import chkan.ua.data.models.ListEntity
import chkan.ua.data.models.ListWithItems
import chkan.ua.data.models.toEntity
import chkan.ua.data.sources.DataSource
import chkan.ua.data.sources.HistoryDataSource
import chkan.ua.domain.models.Item
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

    override fun getItemsFlowByListId(listId: String): Flow<List<ItemEntity>> = itemsDao.getItemsFlowByListId(listId)

    override suspend fun addList(list: ListEntity) {
        listsDao.addList(list)
    }

    override suspend fun deleteList(listId: String) {
        listsDao.deleteListById(listId)
    }

    override suspend fun updateTitle(editable: Editable) {
        listsDao.updateTitle(editable.id, editable.title)
    }

    override suspend fun getListCount() = listsDao.getListCount()
    override suspend fun getMaxListPosition() = listsDao.getMaxListPosition()
    override suspend fun getMaxItemPosition() = itemsDao.getMaxItemPosition()

    override suspend fun moveToTop(id: String, position: Int) {
        listsDao.moveToTop(id,position)
    }

    override suspend fun addItem(item: ItemEntity) {
        itemsDao.addItem(item)
    }

    override suspend fun deleteItem(itemId: String) {
        itemsDao.deleteById(itemId)
    }

    override suspend fun moveItemToTop(id: String, position: Int) {
        itemsDao.moveToTop(id,position)
    }

    override suspend fun getListWithItemsById(listId: String): ListWithItems? {
        return listsDao.getListWithItemsById(listId)
    }

    override suspend fun deleteItemsOfList(listId: String) {
        itemsDao.deleteItemsOfList(listId)
    }

    override suspend fun addItems(items: List<Item>) {
        itemsDao.addItems(items.map { it.toEntity() })
    }

    override suspend fun markItemReady(itemId: String, state: Boolean) {
        val stateAsInt = if (state) 1 else 0
        itemsDao.markItemReady(itemId,stateAsInt)
    }

    override suspend fun clearReadyItems(listId: String) {
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