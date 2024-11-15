package chkan.ua.data.repos

import chkan.ua.data.models.mapToItems
import chkan.ua.data.models.toEntity
import chkan.ua.data.sources.DataSource
import chkan.ua.domain.models.Item
import chkan.ua.domain.repos.ItemsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ItemsRepositoryImpl @Inject constructor (private val dataSource: DataSource) : ItemsRepository {

    override fun getListWithItemsFlowById(listId: Int): Flow<List<Item>> {
        return dataSource.getItemsFlowByListId(listId).map { it.mapToItems() }
    }

    override fun getReadyItemsFlowByListId(listId: Int): Flow<List<Item>> {
        return dataSource.getReadyItemsFlowByListId(listId).map { it.mapToItems() }
    }

    override suspend fun addItem(item: Item) {
        dataSource.addItem(item.toEntity())
    }

    override suspend fun deleteItem(itemId: Int) {
        dataSource.deleteItem(itemId)
    }
}