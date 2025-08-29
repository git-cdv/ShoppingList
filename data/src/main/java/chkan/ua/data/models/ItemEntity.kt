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
    @PrimaryKey
    val itemId: String,
    val listId: String,
    val content: String,
    val position: Int = 0,
    val isReady: Boolean = false,
    val note: String? = null
)

fun List<ItemEntity>.mapToItems() : List<Item>{
    return this.map { Item(it.itemId, content = it.content, listId = it.listId, position = it.position, isReady = it.isReady, note = it.note) }
}

fun Item.toEntity() : ItemEntity {
    return ItemEntity(itemId = this.itemId, listId = this.listId, content = this.content, isReady = this.isReady, position = this.position, note = this.note)
}