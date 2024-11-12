package chkan.ua.domain.usecases.lists

import chkan.ua.core.interfaces.FlowUseCase
import chkan.ua.domain.repos.ListsRepository
import javax.inject.Inject

class GetListFlowUseCase @Inject constructor(
    private val listsRepository: ListsRepository
) : FlowUseCase<Int> {

    override fun run(config: Int) = listsRepository.getListWithItemsFlowById(config)

    override fun getErrorReason() = "Failed to get lists"
}