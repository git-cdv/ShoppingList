package chkan.ua.domain.models

data class ListItems (
    val id: Int,
    val title: String,
    val position: Int,
    val items: List<Item>,
/*    val createdBy: String,
    val createdAt: String,
    val shareCode: String,
    val membersIds: List<String>*/
)
