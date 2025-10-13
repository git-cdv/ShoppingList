package chkan.ua.domain

interface Analytics {
    fun logEvent(eventName: String, parameters: Map<String, Any>? = null)
    fun setUserId(userId: String)
    fun setUserProperty(name: String, value: String)
    fun logScreenView(screenName: String, screenClass: String? = null)
}