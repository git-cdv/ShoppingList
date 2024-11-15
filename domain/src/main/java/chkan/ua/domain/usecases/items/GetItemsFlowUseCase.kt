package chkan.ua.domain.usecases.items

import chkan.ua.core.interfaces.FlowUseCase
import chkan.ua.domain.repos.ItemsRepository
import javax.inject.Inject

class GetItemsFlowUseCase @Inject constructor(
    private val itemsRepository: ItemsRepository
) : FlowUseCase<Int> {

    override fun run(config: Int) = itemsRepository.getListWithItemsFlowById(config)

    override fun getErrorReason(config: Int?) = "Failed to get lists"
}