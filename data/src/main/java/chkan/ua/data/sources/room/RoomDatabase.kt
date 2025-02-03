package chkan.ua.data.sources.room

import androidx.room.Database
import androidx.room.RoomDatabase
import chkan.ua.data.models.HistoryItemEntity
import chkan.ua.data.models.ItemEntity
import chkan.ua.data.models.ListEntity

@Database(entities = [ListEntity::class, ItemEntity::class, HistoryItemEntity::class], version = 3)
abstract class RoomDatabase: RoomDatabase() {
    abstract val listsDao: ListsDao
    abstract val itemsDao: ItemsDao
    abstract val historyDao: HistoryItemDao
}