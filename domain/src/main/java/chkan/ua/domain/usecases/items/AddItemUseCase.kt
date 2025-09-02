package chkan.ua.domain.usecases.items

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.models.Item
import chkan.ua.domain.repos.ItemsRepository
import chkan.ua.domain.repos.RemoteRepository
import javax.inject.Inject

class AddItemUseCase @Inject constructor(
    private val itemsRepository: ItemsRepository,
    private val remoteRepository: RemoteRepository
) : SuspendUseCase<ItemConfig> {

    override suspend fun invoke(config: ItemConfig) {
        if (config.isShared){
            remoteRepository.addItem(config.item)
        } else {
            itemsRepository.addItem(config.item)
        }
    }

    override fun getErrorReason(config: ItemConfig?) = "Failed to add item: ${config?.item}"

}

data class ItemConfig(val item: Item, val isShared: Boolean)