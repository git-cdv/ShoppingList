package chkan.ua.domain.usecases.lists

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.repos.ListsRepository
import javax.inject.Inject

class AddListUseCase @Inject constructor(
    private val listsRepository: ListsRepository
) : SuspendUseCase<String> {

    override suspend fun run(config: String) {
        listsRepository.addList(config)
    }

    override fun getErrorReason(config: String?) = "Failed to add list"
}
