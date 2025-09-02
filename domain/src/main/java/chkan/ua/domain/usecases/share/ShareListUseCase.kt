package chkan.ua.domain.usecases.share

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
                Result.success(firestoreId)
            }
        } catch (e: Exception) {
            logger.e(e)
            Result.failure(e)
        }
    }
}
