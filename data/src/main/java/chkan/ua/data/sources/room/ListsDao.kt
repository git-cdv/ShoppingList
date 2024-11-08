package chkan.ua.data.sources.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
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
    @Query("SELECT * FROM lists")
    fun getListsWithItemsFlow(): Flow<List<ListWithItems>>

    @Query("SELECT COUNT(*) FROM lists")
    suspend fun getListCount(): Int
}