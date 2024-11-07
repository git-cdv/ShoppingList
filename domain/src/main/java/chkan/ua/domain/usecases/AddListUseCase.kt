package chkan.ua.domain.usecases

import chkan.ua.core.interfaces.UseCase
import chkan.ua.domain.repos.ListsRepository
import javax.inject.Inject

class AddListUseCase @Inject constructor(
    private val listsRepository: ListsRepository
) : UseCase<String> {

    override suspend fun run(args: String) {
        listsRepository.addList(args)
    }

    override fun getErrorReason() = "Failed to add list"
}