package chkan.ua.domain.usecases.lists

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.repos.ListsRepository
import javax.inject.Inject

class DeleteListUseCase @Inject constructor(
    private val listsRepository: ListsRepository
) : SuspendUseCase<Int> {

    override suspend fun run(config: Int) {
        listsRepository.deleteList(config)
    }

    override fun getErrorReason(config: Int?) = "Failed to delete list"
}