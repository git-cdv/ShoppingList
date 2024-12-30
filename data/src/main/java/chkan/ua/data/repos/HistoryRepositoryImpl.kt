package chkan.ua.data.repos

import chkan.ua.data.models.mapToHistoryItem
import chkan.ua.data.sources.DataSource
import chkan.ua.domain.models.HistoryItem
import chkan.ua.domain.repos.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor (private val dataSource: DataSource) : HistoryRepository {

    override fun getHistory(listId: Int) : Flow<List<HistoryItem>> {
        val historyFlow = dataSource.getHistory()
        val itemsFlow = dataSource.getItemsFlowByListId(listId)

        return combine(historyFlow, itemsFlow) { historyList, itemsList ->
            val contents = itemsList.map { it.content }.toSet()
            historyList.filter { it.name !in contents }.mapToHistoryItem()
        }
    }

    override suspend fun incrementOrInsertInHistory(name: String) {
        dataSource.incrementOrInsertInHistory(name)
    }

}