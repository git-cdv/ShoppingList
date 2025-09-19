package chkan.ua.domain.usecases.share

import chkan.ua.domain.repos.PreferencesDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HasSharedListsUseCase @Inject constructor(private val dataStore: PreferencesDataStore) {

    companion object{
        const val HAS_SHARED_LISTS_KEY = "has_shared_lists"
    }

    suspend fun getState(): Boolean {
        return dataStore.getBoolean(HAS_SHARED_LISTS_KEY) == true
    }

    suspend fun setState(state: Boolean) {
        dataStore.putBoolean(HAS_SHARED_LISTS_KEY, state)
    }

    fun observe(): Flow<Boolean?>{
        return dataStore.getBooleanFlow(HAS_SHARED_LISTS_KEY)
    }
}