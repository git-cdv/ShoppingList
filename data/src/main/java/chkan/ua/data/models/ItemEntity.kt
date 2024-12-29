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
    val position: Int = 0,
    val isReady: Boolean = false
)

fun List<ItemEntity>.mapToItems() : List<Item>{
    return this.map { Item(it.itemId, content = it.content, listId = it.listId, position = it.position, isReady = it.isReady) }
}

fun Item.toEntity() : ItemEntity {
    return ItemEntity(listId = this.listId, content = this.content, position = this.position)
}