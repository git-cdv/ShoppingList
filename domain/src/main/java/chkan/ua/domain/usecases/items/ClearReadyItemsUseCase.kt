package chkan.ua.domain.usecases.items

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.repos.ItemsRepository
import chkan.ua.domain.repos.RemoteRepository
import javax.inject.Inject

class ClearReadyItemsUseCase @Inject constructor(
    private val itemsRepository: ItemsRepository,
    private val remoteRepository: RemoteRepository
) : SuspendUseCase<ClearReadyConfig> {

    override suspend fun invoke(config: ClearReadyConfig) {
        if (config.isShared){
            remoteRepository.clearReadyItems(config.listId)
        } else {
            itemsRepository.clearReadyItems(config.listId)
        }
    }

    override fun getErrorReason(config: ClearReadyConfig?) = "Failed to clear ready items with listId: $config"
}

data class ClearReadyConfig(val listId: String, val isShared: Boolean)