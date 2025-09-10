package chkan.ua.shoppinglist.core.analytics

interface AnalyticsEvent {
    val type: String
    val properties: Map<String, Any?>?
}