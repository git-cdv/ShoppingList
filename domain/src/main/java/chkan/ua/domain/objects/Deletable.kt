package chkan.ua.domain.objects

data class Deletable(
    val id: String = "id",
    val isShared: Boolean = false,
) {
    init {
        require(id.isNotBlank()) { "ID must not be blank. Provided value: $id" }
    }
}