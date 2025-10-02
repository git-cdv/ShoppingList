package chkan.ua.domain.usecases.share

import chkan.ua.core.exceptions.ResourceCode
import chkan.ua.core.exceptions.UserMessageException
import chkan.ua.domain.Logger
import chkan.ua.domain.repos.RemoteRepository
import chkan.ua.domain.usecases.auth.AuthManager
import javax.inject.Inject


class UnfollowUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val authManager: AuthManager,
     private val logger: Logger
) {
    suspend operator fun invoke(listId: String): Result<Unit> {
        return try {
            val userId = authManager.getCurrentUserId()
            if (userId == null) {
                throw UserMessageException(ResourceCode.UNKNOWN_ERROR,"User is not authenticated")
            } else {
                remoteRepository.unfollow(userId,listId)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(UserMessageException(ResourceCode.UNKNOWN_ERROR))
        }
    }
}