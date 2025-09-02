package chkan.ua.domain.usecases.lists

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.objects.Deletable
import chkan.ua.domain.repos.ListsRepository
import chkan.ua.domain.repos.RemoteRepository
import javax.inject.Inject

class DeleteListUseCase @Inject constructor(
    private val listsRepository: ListsRepository,
    private val remoteRepository: RemoteRepository
) : SuspendUseCase<Deletable> {

    override suspend fun invoke(config: Deletable) {
        if (config.isShared){
            remoteRepository.deleteList(config.id)
        } else {
            listsRepository.deleteList(config.id)
        }
    }

    override fun getErrorReason(config: Deletable?) = "Failed to delete list"
}