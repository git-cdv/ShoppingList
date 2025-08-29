package chkan.ua.domain.models

data class ListSummary(
    val id: String,
    val title: String,
    val totalItems: Int,
    val readyItems: Int,
    val isOwner: Boolean
)
