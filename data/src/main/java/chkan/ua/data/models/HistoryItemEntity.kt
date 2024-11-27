package chkan.ua.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import chkan.ua.domain.models.HistoryItem

@Entity
data class HistoryItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val count: Int,
)

fun List<HistoryItemEntity>.mapToHistoryItem(): List<HistoryItem> {
    return this.map { HistoryItem(id = it.id, name = it.name) }
}
