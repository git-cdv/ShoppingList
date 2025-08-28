package chkan.ua.domain.usecases.items

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.repos.ItemsRepository
import chkan.ua.domain.repos.RemoteRepository
import javax.inject.Inject

class DeleteItemUseCase @Inject constructor(
    private val itemsRepository: ItemsRepository,
    private val remoteRepository: RemoteRepository
) : SuspendUseCase<ItemConfig> {

    override suspend fun invoke(config: ItemConfig) {
        if (config.isShared){
            remoteRepository.deleteItem(config.item.listId, config.item.itemId,config.item.isReady)
        } else {
            itemsRepository.deleteItem(config.item.itemId)
        }
    }

    override fun getErrorReason(config: ItemConfig?) = "Failed to delete itemId: $config"
}