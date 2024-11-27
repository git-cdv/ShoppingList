package chkan.ua.data.repos

import chkan.ua.data.models.mapToHistoryItem
import chkan.ua.data.sources.DataSource
import chkan.ua.domain.repos.HistoryRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor (private val dataSource: DataSource) : HistoryRepository {

    override fun getHistory() = dataSource.getHistory().map { it.mapToHistoryItem() }

    override suspend fun incrementOrInsertInHistory(name: String) {
        dataSource.incrementOrInsertInHistory(name)
    }

}