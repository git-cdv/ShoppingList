package chkan.ua.domain.models

data class Item(
    val itemId: Int,
    val content: String,
    val position: Int,
    val isReady: Boolean
)
