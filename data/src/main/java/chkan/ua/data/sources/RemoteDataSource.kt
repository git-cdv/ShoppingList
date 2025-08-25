package chkan.ua.data.sources

import chkan.ua.domain.models.Item
import chkan.ua.domain.models.ListItems

interface RemoteDataSource {
    suspend fun createSharedList(userId:String, list: ListItems): String
}