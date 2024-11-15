package chkan.ua.domain.usecases.items

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.repos.ItemsRepository
import javax.inject.Inject

class MarkReadyItemUseCase @Inject constructor(
    private val itemsRepository: ItemsRepository
) : SuspendUseCase<MarkReadyConfig> {

    override suspend fun run(config: MarkReadyConfig) {
        itemsRepository.markItemReady(config.itemId, config.state)
    }

    override fun getErrorReason(config: MarkReadyConfig?) = "Failed to mark ready itemId: ${config?.itemId}"
}

data class MarkReadyConfig(val itemId: Int, val state: Boolean)