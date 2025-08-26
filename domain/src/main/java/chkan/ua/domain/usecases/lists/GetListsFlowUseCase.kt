package chkan.ua.domain.usecases.lists

import chkan.ua.core.interfaces.FlowUseCase
import chkan.ua.domain.models.toUiModels
import chkan.ua.domain.repos.ListsRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetListsFlowUseCase @Inject constructor(
    private val listsRepository: ListsRepository
) : FlowUseCase<Unit> {

    override fun invoke(config: Unit) = listsRepository.getListsWithItemsFlow().map { it.toUiModels() }

    override fun getErrorReason(config: Unit?) = "Failed to get lists"
}