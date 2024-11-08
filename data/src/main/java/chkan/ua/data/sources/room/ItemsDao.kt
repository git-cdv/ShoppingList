package chkan.ua.data.sources.room

import androidx.room.Insert
import chkan.ua.data.models.ItemEntity

interface ItemsDao {
    @Insert
    suspend fun addItem(item: ItemEntity)
}