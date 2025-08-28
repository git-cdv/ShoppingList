package chkan.ua.domain.usecases.share

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.models.ListItemsUi
import chkan.ua.domain.models.ListProgress
import chkan.ua.domain.models.ListSummary
import chkan.ua.domain.repos.RemoteRepository
import chkan.ua.domain.usecases.auth.AuthManager
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class GetSharedListsFlowUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val authManager: AuthManager,
) : SuspendUseCase<Unit> {

    override suspend fun invoke(config: Unit) =
        remoteRepository.getAllListsSummaryFlow(authManager.getCurrentUserId()!!)
            .map { it.toUiModels() }
            .catch { error -> throw Exception(getErrorReason(config), error) }

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
            isShared = true
        )
    }
}
