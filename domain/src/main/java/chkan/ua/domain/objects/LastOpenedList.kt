package chkan.ua.domain.objects

data class LastOpenedList(val id: String, val title: String, val isShared: Boolean) {
    init {
        require(id.isNotBlank()) { "ID LastOpenedList must not be blank. Provided value: $id" }
        require(title.isNotBlank()) { "Title LastOpenedList must not be blank." }
    }
}