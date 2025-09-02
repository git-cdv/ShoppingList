package chkan.ua.shoppinglist.ui.screens.paywall

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallModalBottomSheet(
    paywallUiState: PaywallUiState,
    list:List<PaywallItem>,
    snackbarHostState: SnackbarHostState,
    onItemSelected: (String)->Unit,
    onSubscribe: ()->Unit,
    onSubscribeRestore: ()->Unit,
    onDismiss: () -> Unit,
    onClosePaywall: () -> Unit,
    modifier: Modifier = Modifier
) {

    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue ->
            !(paywallUiState.isHardPaywall && newValue == SheetValue.Hidden)
        }
    )

    LaunchedEffect(sheetState.currentValue) {
        if (paywallUiState.isHardPaywall && sheetState.currentValue == SheetValue.Hidden) {
            scope.launch {
                sheetState.show()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = { if (!paywallUiState.isHardPaywall) onDismiss()},
        sheetState = sheetState,
        containerColor = Color.Transparent,
        contentWindowInsets = { WindowInsets(bottom = 0) },
        dragHandle = null
    ) {
        PaywallBox(
            paywallUiState = paywallUiState,
            modifier = modifier,
            snackbarHostState = snackbarHostState,
            list = list,
            onItemSelected = onItemSelected,
            onSubscribe = onSubscribe,
            onSubscribeRestore = onSubscribeRestore,
            onClose = onClosePaywall
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun PaywallModalPreview() {
    ShoppingListTheme {
        PaywallModalBottomSheet(
            PaywallUiState(),
            listOf(),
            snackbarHostState = SnackbarHostState(),
            onItemSelected = {},
            onSubscribe = {},
            onSubscribeRestore = {},
            onDismiss = {},
            onClosePaywall = {},
        )
    }
}