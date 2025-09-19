package chkan.ua.shoppinglist.core.remoteconfigs

import chkan.ua.domain.Logger
import chkan.ua.shoppinglist.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigManager @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    private val logger: Logger
) {
    private val _configState = MutableStateFlow<ConfigState>(ConfigState.Loading)
    val configState = _configState.asStateFlow()

    init {
        fetchAndActivate()
    }

    sealed class ConfigState {
        object Loading : ConfigState()
        object Success : ConfigState()
        data class Error(val message: String) : ConfigState()
    }

    fun fetchAndActivate() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _configState.value = ConfigState.Success
                    if (BuildConfig.DEBUG) { getAllConfigsDebag() }
                } else {
                    val errorMessage = task.exception?.message ?: "Unknown error"
                    _configState.value = ConfigState.Error(errorMessage)
                    logger.e(Exception("Config fetch failed: $errorMessage"))
                }
            }
    }

    fun isLegalEnabled() = getBoolean(RemoteConfigDefaults.Keys.IS_LEGAL)

    fun getBoolean(key: String): Boolean {
        return try {
            remoteConfig.getBoolean(key)
        } catch (e: Exception) {
            logger.e(e,"Error getting $key from remote config")
            RemoteConfigDefaults.getDefaultValue(key)
        }
    }

    private fun getAllConfigsDebag() {
        try {
            val all = remoteConfig.all.mapValues { it.value.asString() }
            logger.d("RemoteConfig","All remote configs: $all")
        } catch (e: Exception) {
            logger.e(e,"Error getting all configs")
        }
    }
}