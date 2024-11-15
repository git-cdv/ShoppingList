package chkan.ua.domain.usecases.lists

import chkan.ua.core.interfaces.FlowUseCase
import chkan.ua.domain.repos.ListsRepository
import javax.inject.Inject

class GetListsFlowUseCase @Inject constructor(
    private val listsRepository: ListsRepository
) : FlowUseCase<Unit> {

    override fun run(config: Unit) = listsRepository.getListsWithItemsFlow()

    override fun getErrorReason(config: Unit?) = "Failed to get lists"
}