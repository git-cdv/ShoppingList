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
    @Query("SELECT * FROM items WHERE listId = :listId ORDER BY position ASC")
    fun getItemsFlowByListId(listId: Int): Flow<List<ItemEntity>>

    @Query("DELETE FROM items WHERE itemId = :itemId")
    suspend fun deleteById(itemId: Int)

    @Query("DELETE FROM items WHERE listId = :listId AND isReady = 1")
    suspend fun clearReadyItems(listId: Int)

    @Query("UPDATE items SET isReady = :state WHERE itemId = :itemId")
    suspend fun markItemReady(itemId: Int, state: Int)

    @Query("UPDATE items SET content = :content,note = :note WHERE itemId = :itemId")
    suspend fun updateContent(itemId: Int, content: String, note: String? = null)

    @Query("SELECT MAX(position) FROM items")
    suspend fun getMaxItemPosition(): Int?

    @Transaction
    suspend fun moveToTop(id: Int, position: Int) {
        shiftPositions(position)
        moveItemToTop(id)
    }

    @Query("UPDATE items SET position = position + 1 WHERE position < :currentPosition")
    suspend fun shiftPositions(currentPosition: Int)

    @Query("UPDATE items SET position = 0 WHERE itemId = :id")
    suspend fun moveItemToTop(id: Int)
}