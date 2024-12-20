package chkan.ua.domain.usecases.lists

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.repos.ListsRepository
import javax.inject.Inject

class MoveToTopUseCase @Inject constructor(
    private val listsRepository: ListsRepository
) : SuspendUseCase<MoveTop> {

    override suspend fun run(config: MoveTop) {
        listsRepository.moveToTop(config.id, config.position)
    }

    override fun getErrorReason(config: MoveTop?) = "Failed to move top"
}

data class MoveTop(val id: Int, val position: Int)