package chkan.ua.domain.usecases.session

import chkan.ua.domain.repos.PreferencesDataStore
import javax.inject.Inject


class IsInvitedUseCase @Inject constructor(private val dataStore: PreferencesDataStore) {

    companion object{
        const val IS_INVITED_KEY = "is_invited"
    }

    suspend fun get(): Boolean {
        return dataStore.getBoolean(IS_INVITED_KEY) == false
    }

    suspend fun set(state: Boolean) {
        dataStore.putBoolean(IS_INVITED_KEY, state)
    }

}