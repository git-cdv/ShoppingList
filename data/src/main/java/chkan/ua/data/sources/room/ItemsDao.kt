package chkan.ua.data.sources.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import chkan.ua.data.models.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemsDao {
    @Insert
    suspend fun addItem(item: ItemEntity)

    @Transaction
    @Query("SELECT * FROM items WHERE listId = :listId AND isReady = 0")
    fun getItemsFlowByListId(listId: Int): Flow<List<ItemEntity>>

    @Transaction
    @Query("SELECT * FROM items WHERE listId = :listId AND isReady = 1")
    fun getReadyItemsFlowByListId(listId: Int): Flow<List<ItemEntity>>

    @Query("DELETE FROM items WHERE itemId = :itemId")
    fun deleteById(itemId: Int)
}