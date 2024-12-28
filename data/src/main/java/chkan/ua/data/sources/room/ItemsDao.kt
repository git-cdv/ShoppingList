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
    @Query("SELECT * FROM items WHERE listId = :listId")
    fun getItemsFlowByListId(listId: Int): Flow<List<ItemEntity>>

    @Query("DELETE FROM items WHERE itemId = :itemId")
    suspend fun deleteById(itemId: Int)

    @Query("DELETE FROM items WHERE listId = :listId AND isReady = 1")
    suspend fun clearReadyItems(listId: Int)

    @Query("UPDATE items SET isReady = :state WHERE itemId = :itemId")
    suspend fun markItemReady(itemId: Int, state: Int)

    @Query("UPDATE items SET content = :content WHERE itemId = :itemId")
    suspend fun updateContent(itemId: Int, content: String)
}