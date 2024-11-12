package chkan.ua.data.sources.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import chkan.ua.data.models.ItemEntity
import chkan.ua.data.models.ListWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemsDao {
    @Insert
    suspend fun addItem(item: ItemEntity)

    @Transaction
    @Query("SELECT * FROM items WHERE listId = :listId")
    fun getItemsFlowByListId(listId: Int): Flow<List<ItemEntity>>
}