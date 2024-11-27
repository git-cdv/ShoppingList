package chkan.ua.domain.usecases.history

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.repos.HistoryRepository
import javax.inject.Inject

class AddItemInHistoryUseCase @Inject constructor(
    private val historyItem: HistoryRepository
) : SuspendUseCase<String> {

    override suspend fun run(config: String) {
        historyItem.incrementOrInsertInHistory(config)
    }

    override fun getErrorReason(config: String?) = "Failed to add item in history: $config"

}