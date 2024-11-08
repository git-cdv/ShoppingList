package chkan.ua.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import chkan.ua.domain.models.Item

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = ListEntity::class,
            parentColumns = ["listId"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val itemId: Int = 0,
    val listId: Int,
    val content: String,
    val position: Int,
    val isReady: Boolean
)

fun List<ItemEntity>.mapToItems() : List<Item>{
    return this.map { Item(it.itemId, content = it.content, position = it.position, isReady = it.isReady) }
}