package chkan.ua.domain.models

data class ListItems (
    val id: String,
    val title: String,
    val position: Int,
    val items: List<Item>,
    val isShared: Boolean,
)
