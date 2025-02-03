package chkan.ua.domain.usecases.history

import chkan.ua.core.interfaces.FlowUseCase
import chkan.ua.domain.repos.HistoryRepository
import javax.inject.Inject

class GetHistoryFlowUseCase @Inject constructor(
    private val historyItem: HistoryRepository
) : FlowUseCase<Int> {

    override fun run(config: Int) = historyItem.getHistory(config)

    override fun getErrorReason(config: Int?): String = "Failed to get history"
}