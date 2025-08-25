package chkan.ua.domain.repos

import chkan.ua.domain.models.ListItems

interface RemoteRepository {
    suspend fun createSharedList(userId:String, list: ListItems): String
}