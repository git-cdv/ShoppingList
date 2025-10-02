package chkan.ua.domain.usecases.history

import chkan.ua.core.interfaces.FlowUseCase
import chkan.ua.domain.Logger
import chkan.ua.domain.repos.HistoryRepository
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class GetHistoryFlowUseCase @Inject constructor(
    private val historyItem: HistoryRepository,
    private val logger: Logger
) : FlowUseCase<String> {

    override fun invoke(config: String) = historyItem
        .getHistory(config)
        .catch { e ->
            logger.e(e, getErrorReason(config))
            emit(emptyList())
        }

    override fun getErrorReason(config: String?): String = "Failed to get history"
}