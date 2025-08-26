package chkan.ua.data.repos

import chkan.ua.data.models.mapToItems
import chkan.ua.data.models.toEntity
import chkan.ua.data.sources.DataSource
import chkan.ua.domain.models.Item
import chkan.ua.domain.objects.Editable
import chkan.ua.domain.repos.ItemsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ItemsRepositoryImpl @Inject constructor (private val dataSource: DataSource) : ItemsRepository {

    override fun getListWithItemsFlowById(listId: String): Flow<List<Item>> {
        return dataSource.getItemsFlowByListId(listId).map { it.mapToItems() }
    }

    override suspend fun addItem(item: Item) {
        val position = (dataSource.getMaxItemPosition() ?: -1) + 1
        val itemWithPosition = item.copy(position = position)
        dataSource.addItem(itemWithPosition.toEntity())
    }

    override suspend fun deleteItem(itemId: String) {
        dataSource.deleteItem(itemId)
    }

    override suspend fun updateContent(editable: Editable) {
        dataSource.updateContent(editable)
    }

    override suspend fun clearReadyItems(listId: String) {
        dataSource.clearReadyItems(listId)
    }

    override suspend fun markItemReady(itemId: String, state: Boolean) {
        dataSource.markItemReady(itemId,state)
    }

    override suspend fun moveToTop(itemId: String, position: Int) = dataSource.moveItemToTop(itemId, position)
    override suspend fun deleteItemsOfList(listId: String) = dataSource.deleteItemsOfList(listId)
}