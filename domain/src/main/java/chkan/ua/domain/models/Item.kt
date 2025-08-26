package chkan.ua.domain.models

data class Item(
    val itemId: String,
    val content: String,
    val listId: String,
    val position: Int = 0,
    val isReady: Boolean = false,
    val note: String? = null
)
