package chkan.ua.domain.usecases.history

import chkan.ua.core.interfaces.FlowUseCase
import chkan.ua.domain.repos.HistoryRepository
import javax.inject.Inject

class GetHistoryFlowUseCase @Inject constructor(
    private val historyItem: HistoryRepository
) : FlowUseCase<String> {

    override fun invoke(config: String) = historyItem.getHistory(config)

    override fun getErrorReason(config: String?): String = "Failed to get history"
}