package chkan.ua.data.repos

import chkan.ua.data.sources.RemoteDataSource
import chkan.ua.domain.models.ListItems
import chkan.ua.domain.repos.RemoteRepository
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor (
    private val dataSource: RemoteDataSource
) : RemoteRepository {
    override suspend fun createSharedList(userId:String, list: ListItems) = dataSource.createSharedList(userId, list)
}