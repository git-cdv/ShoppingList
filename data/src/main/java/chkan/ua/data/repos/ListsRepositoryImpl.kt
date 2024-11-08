package chkan.ua.data.repos

import chkan.ua.data.sources.DataSource
import chkan.ua.domain.repos.ListsRepository
import javax.inject.Inject

class ListsRepositoryImpl @Inject constructor (private val dataSource: DataSource) : ListsRepository {
    override suspend fun addList(name: String) {}
}