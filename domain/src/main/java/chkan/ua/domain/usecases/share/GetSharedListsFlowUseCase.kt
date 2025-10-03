package chkan.ua.domain.usecases.share

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.core.models.ListRole
import chkan.ua.domain.Logger
import chkan.ua.domain.models.ListItemsUi
import chkan.ua.domain.models.ListProgress
import chkan.ua.domain.models.ListSummary
import chkan.ua.domain.repos.RemoteRepository
import chkan.ua.domain.usecases.auth.AuthManager
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetSharedListsFlowUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val authManager: AuthManager,
    private val logger: Logger
) : SuspendUseCase<Unit> {

    override suspend fun invoke(config: Unit) =
        authManager.getCurrentUserId()?.let { userId ->
            remoteRepository.getAllListsSummaryFlow(userId)
                .map { it.toUiModels() }
                .catch { error ->
                    logger.e(error as Exception,getErrorReason())
                    emit(emptyList())
                }
        } ?: flowOf(emptyList())


    override fun getErrorReason(config: Unit?) = "Failed to get shared lists"
}

private fun List<ListSummary>.toUiModels(): List<ListItemsUi> {
    return this.map {
        ListItemsUi(
            id = it.id,
            title = it.title,
            position = 0,
            count = it.totalItems,
            readyCount = it.readyItems,
            progress = ListProgress(it.totalItems, it.readyItems),
            role = if (it.isOwner) ListRole.SHARED_OWNER else ListRole.SHARED_MEMBER
        )
    }
}
