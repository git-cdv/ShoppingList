package chkan.ua.data.repos

import chkan.ua.data.sources.RemoteDataSource
import chkan.ua.domain.models.Item
import chkan.ua.domain.models.ListItems
import chkan.ua.domain.models.ListSummary
import chkan.ua.domain.objects.Editable
import chkan.ua.domain.repos.RemoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(
    private val dataSource: RemoteDataSource
) : RemoteRepository {
    override suspend fun createSharedList(userId: String, list: ListItems) =
        dataSource.createSharedList(userId, list)

    override fun getListWithItemsFlowById(listId: String): Flow<List<Item>> =
        dataSource.getListWithItemsFlowById(listId)

    override fun getAllListsSummaryFlow(userId: String): Flow<List<ListSummary>> = dataSource.getAllListsSummaryFlow(userId)

    override suspend fun markItemReady(
        listId: String,
        itemId: String,
        isReady: Boolean
    ) {
        dataSource.markItemReady(listId, itemId, isReady)
    }

    override suspend fun addItem(item: Item) {
        dataSource.addItem(item)
    }

    override suspend fun deleteItem(
        listId: String,
        itemId: String,
        wasReady: Boolean
    ) {
        dataSource.deleteItem(listId, itemId, wasReady)
    }

    override suspend fun clearReadyItems(listId: String) {
        dataSource.clearReadyItems(listId)
    }

    override suspend fun editItem(
        listId: String,
        editable: Editable
    ) {
        dataSource.editItem(listId, editable)
    }

    override suspend fun editList(config: Editable) {
        dataSource.editList(config)
    }

    override suspend fun deleteList(listId: String){
        dataSource.deleteList(listId)
    }

    override suspend fun getListWithItemsById(listId: String) = dataSource.getListWithItemsById(listId)
}