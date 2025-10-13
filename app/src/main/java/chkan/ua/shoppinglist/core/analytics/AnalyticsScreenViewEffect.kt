package chkan.ua.shoppinglist.core.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import chkan.ua.domain.Analytics

@Composable
fun AnalyticsScreenViewEffect(
    screenName: String,
    analytics: Analytics = LocalAnalytics.current,
) = DisposableEffect(Unit) {
    analytics.logScreenView(screenName)
    onDispose {}
}