package chkan.ua.shoppinglist.ui.screens.paywall

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallModalBottomSheet(
    sheetState: SheetState,
    paywallUiState: PaywallUiState,
    list:List<PaywallItem>,
    isReview: Boolean,
    snackbarHostState: SnackbarHostState,
    onEvent: (PaywallUiEvent)->Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity

    LaunchedEffect(paywallUiState.event) {
        val event = paywallUiState.event
        when (event) {
            PaywallEvent.ProductPurchased -> { onDismiss() }
            PaywallEvent.RestorePurchasesFailed -> {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.error_restore_purchases_failed_message),
                    actionLabel = null
                )
            }
            null -> {}
        }
        onEvent(PaywallUiEvent.PaywallEventConsumed)
    }


    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        contentWindowInsets = { WindowInsets(bottom = 0) },
        dragHandle = null
    ) {
        PaywallBox(
            isReview = isReview,
            modifier = modifier,
            snackbarHostState = snackbarHostState,
            list = list,
            onItemSelected = { onEvent(PaywallUiEvent.ProductSelected(it)) },
            onSubscribe = { onEvent(PaywallUiEvent.Subscribe(activity)) },
            onSubscribeRestore = { onEvent(PaywallUiEvent.SubscribeRestore) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun PaywallModalPreview() {
    ShoppingListTheme {
        PaywallModalBottomSheet(
            sheetState = rememberModalBottomSheetState(true),
            paywallUiState = PaywallUiState(),
            listOf(),
            false,
            snackbarHostState = SnackbarHostState(),
            onEvent = {},
            onDismiss = {},
        )
    }
}