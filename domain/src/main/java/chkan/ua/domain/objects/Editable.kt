package chkan.ua.domain.objects

data class Editable(
    val id: String = "id",
    val title: String = "Title",
    val note: String? = null,
    val listId: String? = null,
    val isShared: Boolean = false,
) {
    init {
        require(id.isNotBlank()) { "ID must not be blank. Provided value: $id" }
        require(title.isNotBlank()) { "Title must not be blank." }
    }
}