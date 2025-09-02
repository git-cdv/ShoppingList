package chkan.ua.data.sources.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import chkan.ua.domain.repos.PreferencesDataStore
import javax.inject.Inject

class PreferencesDataStoreImpl @Inject constructor (private val dataStore: DataStore<Preferences>) :
    PreferencesDataStore {

    //STRING
    override suspend fun getString(key: String): String? {
        return dataStore.data.first()[stringPreferencesKey(key)]
    }

    override fun getStringFlow(key: String): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)]
        }
    }

    override suspend fun putString(key: String, value: String) {
        dataStore.edit {
            it[stringPreferencesKey(key)] = value
        }
    }

    //BOOLEAN
    override suspend fun getBoolean(key: String): Boolean? {
        return dataStore.data.first()[booleanPreferencesKey(key)]
    }

    override fun getBooleanFlow(key: String): Flow<Boolean?> {
        return dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(key)]
        }
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        dataStore.edit {
            it[booleanPreferencesKey(key)] = value
        }
    }

    //INT
    override suspend fun getInt(key: String): Int? {
        return dataStore.data.first()[intPreferencesKey(key)]
    }

    override suspend fun putInt(key: String, value: Int) {
        dataStore.edit {
            it[intPreferencesKey(key)] = value
        }
    }
}