package chkan.ua.shoppinglist.core.analytics

interface Analytics {
    fun logEvent(event: String, properties: Map<String, Any?>? = null)
    fun logEvent(event: AnalyticsEvent)
}

