package chkan.ua.data.sources.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import chkan.ua.data.models.HistoryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryItemDao {

    @Query("UPDATE HistoryItemEntity SET count = count + 1 WHERE name = :name")
    suspend fun incrementCountByName(name: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(historyItem: HistoryItemEntity): Long

    @Query("SELECT * FROM HistoryItemEntity WHERE count > 1 ORDER BY count DESC")
    fun getHistory(): Flow<List<HistoryItemEntity>>

    @Transaction
    suspend fun incrementOrInsertInHistory(name: String) {
        val updatedRows = incrementCountByName(name)
        if (updatedRows == 0) {
            insert(HistoryItemEntity(name = name, count = 1))
        }
    }
}