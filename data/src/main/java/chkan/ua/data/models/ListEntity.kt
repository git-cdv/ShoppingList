package chkan.ua.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lists")
data class ListEntity (
    @PrimaryKey
    val listId: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "position")
    val position: Int,
    @ColumnInfo(name = "is_shared")
    val isShared: Boolean = false,
    @ColumnInfo(name = "totalItems")
    val totalItems: Int,
    @ColumnInfo(name = "readyItems")
    val readyItems: Int,
)