package chkan.ua.domain.usecases.share

import chkan.ua.domain.repos.ItemsRepository
import chkan.ua.domain.repos.ListsRepository
import chkan.ua.domain.repos.RemoteRepository
import javax.inject.Inject

class StopSharingUseCase @Inject constructor(
    private val listsRepository: ListsRepository,
    private val itemsRepository: ItemsRepository,
    private val remoteRepository: RemoteRepository,
) {

    suspend operator fun invoke(listId: String) {
        val listWithItems = remoteRepository.getListWithItemsById(listId)
        listsRepository.addList(listWithItems.title, listWithItems.id)
        itemsRepository.addItems(listWithItems.items)
        remoteRepository.deleteList(listId)
    }
}