package chkan.ua.domain.repos

interface ListsRepository {
    suspend fun addList(name: String)
}