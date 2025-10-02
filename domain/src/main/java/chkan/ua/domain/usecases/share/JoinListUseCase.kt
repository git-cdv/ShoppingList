package chkan.ua.domain.usecases.share

import chkan.ua.core.exceptions.ResourceCode
import chkan.ua.core.exceptions.UserMessageException
import chkan.ua.domain.repos.RemoteRepository
import chkan.ua.domain.usecases.auth.AuthManager
import javax.inject.Inject

class JoinListUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val authManager: AuthManager,
) {

    suspend operator fun invoke(code: String): Result<Unit> {
        return try {
            val userId = authManager.getCurrentUserId()
            if (userId == null) {
                throw UserMessageException(ResourceCode.UNKNOWN_ERROR,"User is not authenticated")
            } else {
                remoteRepository.joinSharedList(userId,code)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}