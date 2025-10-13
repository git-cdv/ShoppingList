package chkan.ua.domain.usecases.share

import chkan.ua.domain.Analytics
import chkan.ua.domain.Logger
import chkan.ua.domain.repos.ListsRepository
import chkan.ua.domain.repos.RemoteRepository
import chkan.ua.domain.usecases.auth.AuthManager
import javax.inject.Inject

class ShareListUseCase @Inject constructor(
    private val listsRepository: ListsRepository,
    private val remoteRepository: RemoteRepository,
    private val authManager: AuthManager,
    private val logger: Logger,
    private val analytics: Analytics
) {

    suspend operator fun invoke(listId: String): Result<String> {
        return try {
            val listWithItems = listsRepository.getListWithItemsById(listId) ?: throw Exception("List not found")
            val userId = authManager.getCurrentUserId()
            if (userId == null) {
                throw Exception("User is not authenticated")
            } else {
                val firestoreId = remoteRepository.createSharedList(userId,listWithItems)
                listsRepository.deleteList(listId)
                analytics.logEvent("share_list_shared", mapOf("shared_list_name" to listWithItems.title))
                Result.success(firestoreId)
            }
        } catch (e: Exception) {
            logger.e(e)
            Result.failure(e)
        }
    }
}
