package chkan.ua.domain.usecases.items

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.repos.ItemsRepository
import javax.inject.Inject

class ClearReadyItemsUseCase @Inject constructor(
    private val itemsRepository: ItemsRepository
) : SuspendUseCase<Int> {

    override suspend fun run(config: Int) {
        itemsRepository.clearReadyItems(config)
    }

    override fun getErrorReason(config: Int?) = "Failed to clear ready items with listId: $config"
}