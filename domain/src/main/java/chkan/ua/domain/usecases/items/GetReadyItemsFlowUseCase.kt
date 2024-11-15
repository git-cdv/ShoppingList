package chkan.ua.domain.usecases.items

import chkan.ua.core.interfaces.FlowUseCase
import chkan.ua.domain.repos.ItemsRepository
import javax.inject.Inject

class GetReadyItemsFlowUseCase @Inject constructor(
    private val itemsRepository: ItemsRepository
) : FlowUseCase<Int> {

    override fun run(config: Int) = itemsRepository.getReadyItemsFlowByListId(config)

    override fun getErrorReason(config: Int?) = "Failed to get ready lists"
}