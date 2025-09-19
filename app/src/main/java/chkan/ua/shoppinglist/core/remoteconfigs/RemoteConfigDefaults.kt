package chkan.ua.shoppinglist.core.remoteconfigs

object RemoteConfigDefaults {

    object Keys {
        const val IS_LEGAL = "is_legal"
    }

    private val defaults = mapOf(
        Keys.IS_LEGAL to true
    )

    fun getDefaults(): Map<String, Any> = defaults

    @Suppress("UNCHECKED_CAST")
    fun <T> getDefaultValue(key: String): T = defaults[key] as T
}