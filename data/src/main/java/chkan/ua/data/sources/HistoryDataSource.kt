package chkan.ua.data.sources

import chkan.ua.data.models.HistoryItemEntity
import kotlinx.coroutines.flow.Flow

interface HistoryDataSource {
    fun getHistory(): Flow<List<HistoryItemEntity>>
    suspend fun incrementOrInsertInHistory(name: String)
}