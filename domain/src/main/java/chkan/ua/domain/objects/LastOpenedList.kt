package chkan.ua.domain.objects

data class LastOpenedList(val id: Int, val title: String) {
    init {
        require(id > 0) { "ID LastOpenedList must be greater than 0. Provided value: $id" }
        require(title.isNotBlank()) { "Title LastOpenedList must not be blank." }
    }
}