package chkan.ua.domain.usecases.items

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.repos.ItemsRepository
import javax.inject.Inject

class DeleteItemUseCase @Inject constructor(
    private val itemsRepository: ItemsRepository
) : SuspendUseCase<Int> {

    override suspend fun run(config: Int) {
        itemsRepository.deleteItem(config)
    }

    override fun getErrorReason() = "Failed to delete item"
}