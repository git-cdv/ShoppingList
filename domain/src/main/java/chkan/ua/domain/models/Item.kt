package chkan.ua.domain.models

data class Item(
    val itemId: Int = 0,
    val content: String,
    val listId: Int,
    val position: Int = 0,
    val isReady: Boolean = false,
    val note: String? = null
)
