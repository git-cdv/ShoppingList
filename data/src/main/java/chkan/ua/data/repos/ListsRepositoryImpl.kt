package chkan.ua.data.repos

import chkan.ua.domain.repos.ListsRepository
import javax.inject.Inject

class ListsRepositoryImpl @Inject constructor() : ListsRepository {
    override suspend fun addList(name: String) {}
}