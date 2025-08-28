package chkan.ua.domain.usecases.items

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.objects.Editable
import chkan.ua.domain.repos.ItemsRepository
import chkan.ua.domain.repos.RemoteRepository
import javax.inject.Inject

class EditItemUseCase @Inject constructor(
    private val itemsRepository: ItemsRepository,
    private val remoteRepository: RemoteRepository
) : SuspendUseCase<Editable> {

    override suspend fun invoke(config: Editable) {
        if (config.isShared){
            config.listId?.let { remoteRepository.editItem(it,config) }
        } else {
            itemsRepository.updateContent(config)
        }
    }

    override fun getErrorReason(config: Editable?) = "Failed to edit item $config"
}