package chkan.ua.domain.objects

import chkan.ua.core.models.ListRole

data class LastOpenedList(val id: String, val title: String, val role: ListRole) {
    init {
        require(id.isNotBlank()) { "ID LastOpenedList must not be blank. Provided value: $id" }
        require(title.isNotBlank()) { "Title LastOpenedList must not be blank." }
    }
}