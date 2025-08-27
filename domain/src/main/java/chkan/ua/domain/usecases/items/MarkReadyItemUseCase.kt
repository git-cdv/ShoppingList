package chkan.ua.domain.usecases.items

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.repos.ItemsRepository
import chkan.ua.domain.repos.RemoteRepository
import javax.inject.Inject

class MarkReadyItemUseCase @Inject constructor(
    private val itemsRepository: ItemsRepository,
    private val remoteRepository: RemoteRepository
) : SuspendUseCase<MarkReadyConfig> {

    override suspend fun run(config: MarkReadyConfig) {
        if (config.isShared){
            remoteRepository.markItemReady(config.listId, config.itemId,config.state)
        } else {
            itemsRepository.markItemReady(config.itemId, config.state)
        }
    }

    override fun getErrorReason(config: MarkReadyConfig?) = "Failed to mark ready itemId: ${config?.itemId}"
}

data class MarkReadyConfig(val itemId: String, val listId: String, val state: Boolean, val isShared: Boolean)