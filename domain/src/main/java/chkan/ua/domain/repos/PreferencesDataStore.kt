package chkan.ua.domain.repos

import kotlinx.coroutines.flow.Flow

interface PreferencesDataStore {
    suspend fun getBoolean(key: String): Boolean?
    fun getBooleanFlow(key: String): Flow<Boolean?>
    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun getString(key: String): String?
    fun getStringFlow(key: String): Flow<String?>
    suspend fun putString(key: String, value: String)

    suspend fun getInt(key: String): Int?

    suspend fun putInt(key: String, value: Int)
}