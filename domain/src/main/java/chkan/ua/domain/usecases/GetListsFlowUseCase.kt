package chkan.ua.domain.usecases

import chkan.ua.core.interfaces.FlowUseCase
import chkan.ua.domain.repos.ListsRepository
import javax.inject.Inject

class GetListsFlowUseCase @Inject constructor(
    private val listsRepository: ListsRepository
) : FlowUseCase {

    override fun run() = listsRepository.getListsWithItemsFlow()

    override fun getErrorReason() = "Failed to get lists"
}