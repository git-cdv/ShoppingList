package chkan.ua.domain.usecases

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.core.interfaces.UseCase
import chkan.ua.domain.repos.ListsRepository
import javax.inject.Inject

class AddListUseCase @Inject constructor(
    private val listsRepository: ListsRepository
) : SuspendUseCase<AddListConfig> {

    override suspend fun run(config: AddListConfig) {
        listsRepository.addList(config.title, config.position)
    }

    override fun getErrorReason() = "Failed to add list"
}

data class AddListConfig (val title: String, val position: Int)
