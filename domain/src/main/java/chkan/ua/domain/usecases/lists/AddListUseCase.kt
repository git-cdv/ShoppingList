package chkan.ua.domain.usecases.lists

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.repos.ListsRepository
import javax.inject.Inject

class AddListUseCase @Inject constructor(
    private val listsRepository: ListsRepository
) : SuspendUseCase<AddListConfig> {

    override suspend fun run(config: AddListConfig) {
        listsRepository.addList(config.title, config.position)
    }

    override fun getErrorReason(config: AddListConfig?) = "Failed to add list"
}

data class AddListConfig (val title: String, val position: Int)
