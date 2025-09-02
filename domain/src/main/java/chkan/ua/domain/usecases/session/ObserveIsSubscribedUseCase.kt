package chkan.ua.domain.usecases.session

import chkan.ua.domain.Logger
import chkan.ua.domain.repos.PreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class ObserveIsSubscribedUseCase @Inject constructor(
    private val dataStore: PreferencesDataStore,
    private val logger: Logger
) {
    companion object {
        const val IS_SUBSCRIBED_KEY = "is_subscribed"
    }

    operator fun invoke(): Flow<Boolean?> {
        return dataStore.getBooleanFlow(IS_SUBSCRIBED_KEY).catch { e ->
            logger.e(e, e.message)
            emit(null)
        }
    }
}