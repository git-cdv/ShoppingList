package chkan.ua.domain.usecases.items

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.repos.ItemsRepository
import javax.inject.Inject

class ClearReadyItemsUseCase @Inject constructor(
    private val itemsRepository: ItemsRepository
) : SuspendUseCase<String> {

    override suspend fun run(config: String) {
        itemsRepository.clearReadyItems(config)
    }

    override fun getErrorReason(config: String?) = "Failed to clear ready items with listId: $config"
}