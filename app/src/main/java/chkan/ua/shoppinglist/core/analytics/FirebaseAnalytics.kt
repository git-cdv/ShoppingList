package chkan.ua.shoppinglist.core.analytics

import android.os.Bundle
import chkan.ua.domain.Analytics
import chkan.ua.domain.Logger
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseAnalyticsService(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val logger: Logger
) : Analytics {

    override fun logEvent(eventName: String, parameters: Map<String, Any>?) {
        val bundle = Bundle().apply {
            parameters?.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putBoolean(key, value)
                    else -> putString(key, value.toString())
                }
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)
        logger.d("Analytics", "Event: $eventName, Params: $parameters")
    }

    override fun setUserId(userId: String) {
        firebaseAnalytics.setUserId(userId)
    }

    override fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
    }

    override fun logScreenView(screenName: String, screenClass: String?) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            screenClass?.let { putString(FirebaseAnalytics.Param.SCREEN_CLASS, it) }
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        logger.d("Analytics", "logScreenView: $screenName")
    }
}
