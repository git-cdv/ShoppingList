package chkan.ua.domain.usecases.items

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.objects.Editable
import chkan.ua.domain.repos.ItemsRepository
import javax.inject.Inject

class EditItemUseCase @Inject constructor(
    private val itemsRepository: ItemsRepository
) : SuspendUseCase<Editable> {

    override suspend fun run(config: Editable) {
        itemsRepository.updateContent(config)
    }

    override fun getErrorReason(config: Editable?) = "Failed to edit item $config"
}