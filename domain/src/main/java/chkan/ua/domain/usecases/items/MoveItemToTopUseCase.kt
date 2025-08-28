package chkan.ua.domain.usecases.items

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.repos.ItemsRepository
import chkan.ua.domain.usecases.lists.MoveTop
import javax.inject.Inject

class MoveItemToTopUseCase @Inject constructor(
    private val itemsRepository: ItemsRepository
) : SuspendUseCase<MoveTop> {

    override suspend fun invoke(config: MoveTop) {
        itemsRepository.moveToTop(config.id, config.position)
    }

    override fun getErrorReason(config: MoveTop?) = "Failed to move top"
}