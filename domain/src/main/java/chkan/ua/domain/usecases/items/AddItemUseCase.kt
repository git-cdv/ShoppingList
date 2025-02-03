package chkan.ua.domain.usecases.items

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.models.Item
import chkan.ua.domain.repos.ItemsRepository
import javax.inject.Inject

class AddItemUseCase @Inject constructor(
    private val itemsRepository: ItemsRepository
) : SuspendUseCase<Item> {

    override suspend fun run(config: Item) {
        itemsRepository.addItem(config)
    }

    override fun getErrorReason(config: Item?) = "Failed to add item: ${config?.content}"

}