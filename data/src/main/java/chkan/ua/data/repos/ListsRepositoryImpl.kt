package chkan.ua.data.repos

import chkan.ua.data.models.ListEntity
import chkan.ua.data.sources.DataSource
import chkan.ua.domain.models.ListItems
import chkan.ua.domain.objects.Editable
import chkan.ua.domain.repos.ListsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ListsRepositoryImpl @Inject constructor (private val dataSource: DataSource) : ListsRepository {
    override fun getListsWithItemsFlow(): Flow<List<ListItems>> {
        return dataSource.getListsWithItemsFlow().map { it.map { it.mapToListItem() } }
    }

    override suspend fun addList(title: String) {
        val position = (dataSource.getMaxListPosition() ?: -1) + 1
        dataSource.addList(ListEntity(title = title,position = position))
    }

    override suspend fun deleteList(listId: Int) {
        dataSource.deleteList(listId)
    }

    override suspend fun updateTitle(editable: Editable) {
        dataSource.updateTitle(editable)
    }

    override suspend fun getListCount() = dataSource.getListCount()
    override suspend fun moveToTop(id: Int, position: Int) = dataSource.moveToTop(id, position)

}