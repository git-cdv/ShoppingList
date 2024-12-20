package chkan.ua.data.sources.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import chkan.ua.data.models.ListEntity
import chkan.ua.data.models.ListWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface ListsDao {
    @Insert
    suspend fun addList(list: ListEntity)

    @Transaction
    @Query("DELETE FROM lists WHERE listId = :listId")
    suspend fun deleteListById(listId: Int)

    @Transaction
    @Query("SELECT * FROM lists ORDER BY position ASC")
    fun getListsWithItemsFlow(): Flow<List<ListWithItems>>

    @Query("SELECT COUNT(*) FROM lists")
    suspend fun getListCount(): Int

    //for drag and drop
    @Transaction
    suspend fun moveToTop(from: Int) {
        val items = getItemsForPositionChange(from, 0)
        for (item in items) {
            updateList(item.copy(position = item.position + 1))
        }
        updateList(items.first { it.position == from }.copy(position = 0))
    }

    @Update
    suspend fun updateList(list: ListEntity)

    @Query("SELECT * FROM lists WHERE position BETWEEN :start AND :end ORDER BY position ASC")
    suspend fun getItemsForPositionChange(start: Int, end: Int): List<ListEntity>
}