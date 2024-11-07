package chkan.ua.domain.usecases

import chkan.ua.core.interfaces.ErrorReasonGenerator
import chkan.ua.domain.repos.ListsRepository
import javax.inject.Inject

class AddListUseCase @Inject constructor(
    private val listsRepository: ListsRepository
) : ErrorReasonGenerator {

    suspend fun run(name: String) {
        listsRepository.addList(name)
    }

    override fun getErrorReason() = "Failed to add list"
}