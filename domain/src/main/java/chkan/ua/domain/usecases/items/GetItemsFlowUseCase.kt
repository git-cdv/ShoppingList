package chkan.ua.domain.usecases.items

import chkan.ua.core.interfaces.FlowUseCase
import chkan.ua.domain.repos.ItemsRepository
import javax.inject.Inject

class GetItemsFlowUseCase @Inject constructor(
    private val itemsRepository: ItemsRepository
) : FlowUseCase<String> {

    override fun invoke(config: String) = itemsRepository.getListWithItemsFlowById(config)

    override fun getErrorReason(config: String?) = "Failed to get lists"
}