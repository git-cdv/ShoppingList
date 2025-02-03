package chkan.ua.domain.repos

import chkan.ua.domain.models.HistoryItem
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getHistory(listId: Int): Flow<List<HistoryItem>>
    suspend fun incrementOrInsertInHistory(name: String)
}