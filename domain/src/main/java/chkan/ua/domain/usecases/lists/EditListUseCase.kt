package chkan.ua.domain.usecases.lists

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.objects.Editable
import chkan.ua.domain.repos.ListsRepository
import chkan.ua.domain.repos.RemoteRepository
import javax.inject.Inject

class EditListUseCase @Inject constructor(
    private val listsRepository: ListsRepository,
    private val remoteRepository: RemoteRepository
) : SuspendUseCase<Editable> {

    override suspend fun invoke(config: Editable) {
        if (config.isShared){
            remoteRepository.editList(config)
        } else {
            listsRepository.updateTitle(config)
        }
    }

    override fun getErrorReason(config: Editable?) = "Failed to edit list $config"
}