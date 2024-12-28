package chkan.ua.domain.objects

data class Editable(val id: Int, val title: String) {
    init {
        require(id > 0) { "ID must be greater than 0. Provided value: $id" }
        require(title.isNotBlank()) { "Title must not be blank." }
    }
}