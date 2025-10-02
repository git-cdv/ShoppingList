package chkan.ua.domain.usecases.items

import chkan.ua.core.interfaces.FlowUseCase
import chkan.ua.domain.Logger
import chkan.ua.domain.repos.ItemsRepository
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class GetItemsFlowUseCase @Inject constructor(
    private val itemsRepository: ItemsRepository,
    private val logger: Logger
) : FlowUseCase<String> {

    override fun invoke(config: String) = itemsRepository
        .getListWithItemsFlowById(config)
        .catch { e ->
            logger.e(e, getErrorReason(config))
            emit(emptyList())
        }

    override fun getErrorReason(config: String?) = "Failed to get lists"
}