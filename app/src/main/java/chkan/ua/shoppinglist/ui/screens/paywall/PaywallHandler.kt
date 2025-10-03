package chkan.ua.shoppinglist.ui.screens.paywall

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import chkan.ua.domain.Analytics
import chkan.ua.shoppinglist.session.SessionViewModel
import chkan.ua.shoppinglist.ui.screens.paywall.data.PaywallViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallHandler(
    sessionViewModel: SessionViewModel,
    paywallViewModel: PaywallViewModel,
    analytics: Analytics
) {
    val showPaywall by sessionViewModel.showPaywall.collectAsState()
    val paywallItems by paywallViewModel.paywallItemsFlow.collectAsState()
    val paywallUiState by paywallViewModel.paywallUiState.collectAsState()

    val paywallSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(showPaywall) {
        if (showPaywall) {
            analytics.logScreenView("PaywallScreen")
        }
    }

    if (showPaywall) {
        PaywallModalBottomSheet(
            paywallSheetState,
            paywallUiState,
            paywallItems,
            paywallViewModel.isReview(),
            snackbarHostState,
            onEvent = { event -> paywallViewModel.onUiEvent(event) },
            onDismiss = {
                scope.launch {
                    paywallSheetState.hide()
                }.invokeOnCompletion {
                    sessionViewModel.hidePaywall()
                }
            },
            modifier = Modifier.navigationBarsPadding()
        )
    }
}
