package chkan.ua.data.sources.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import chkan.ua.data.models.ItemEntity
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

    @Transaction
    suspend fun moveToTop(id: Int, position: Int) {
        shiftPositions(position)
        moveItemToTop(id)
    }

    @Query("UPDATE lists SET position = position + 1 WHERE position < :currentPosition")
    suspend fun shiftPositions(currentPosition: Int)

    @Query("UPDATE lists SET position = 0 WHERE listId = :id")
    suspend fun moveItemToTop(id: Int)
}