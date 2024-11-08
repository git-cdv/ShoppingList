package chkan.ua.domain.usecases

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.repos.ListsRepository
import javax.inject.Inject

class GetListsCountUseCase @Inject constructor(
    private val listsRepository: ListsRepository
) : SuspendUseCase<Unit> {

    override suspend fun run(config: Unit) = listsRepository.getListCount()

    override fun getErrorReason() = "Failed to get list count"
}