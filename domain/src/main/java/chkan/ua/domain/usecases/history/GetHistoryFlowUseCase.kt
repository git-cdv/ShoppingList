package chkan.ua.domain.usecases.history

import chkan.ua.core.interfaces.FlowUseCase
import chkan.ua.domain.repos.HistoryRepository
import javax.inject.Inject

class GetHistoryFlowUseCase @Inject constructor(
    private val historyItem: HistoryRepository
) : FlowUseCase<Unit> {

    override fun run(config: Unit) = historyItem.getHistory()

    override fun getErrorReason(config: Unit?): String = "Failed to get history"
}