package chkan.ua.shoppinglist.core.analytics

import androidx.compose.runtime.compositionLocalOf
import chkan.ua.domain.Analytics

val LocalAnalytics = compositionLocalOf<Analytics> {
    error("Analytics not provided")
}
