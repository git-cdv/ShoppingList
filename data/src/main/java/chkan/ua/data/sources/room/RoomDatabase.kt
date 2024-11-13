package chkan.ua.data.sources.room

import androidx.room.Database
import androidx.room.RoomDatabase
import chkan.ua.data.models.ItemEntity
import chkan.ua.data.models.ListEntity

@Database(entities = [ListEntity::class, ItemEntity::class], version = 2)
abstract class RoomDatabase: RoomDatabase() {
    abstract val listsDao: ListsDao
    abstract val itemsDao: ItemsDao
}