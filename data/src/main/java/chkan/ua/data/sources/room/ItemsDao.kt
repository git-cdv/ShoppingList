package chkan.ua.data.sources.room

import androidx.room.Dao
import androidx.room.Insert
import chkan.ua.data.models.ItemEntity

@Dao
interface ItemsDao {
    @Insert
    suspend fun addItem(item: ItemEntity)
}