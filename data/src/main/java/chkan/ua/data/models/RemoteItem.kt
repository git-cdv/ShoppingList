package chkan.ua.data.models

data class RemoteItem(
    val itemId: String,
    val content: String,
    val listId: String,
    val isReady: Boolean = false,
    val note: String? = null
)
