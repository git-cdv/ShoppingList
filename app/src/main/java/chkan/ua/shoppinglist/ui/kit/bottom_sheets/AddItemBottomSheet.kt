package chkan.ua.shoppinglist.ui.kit.bottom_sheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.dimensionResource
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.kit.RoundedTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddItemBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    addItem: (String) -> Unit,
    placeholderResId: Int
){
    val scope = rememberCoroutineScope()
    var text by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    var wasKeyboardVisible by remember { mutableStateOf(false) }
    val isKeyboardVisible = WindowInsets.isImeVisible

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {  scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismiss.invoke()
                focusRequester.freeFocus()
            }
        }})
    {
        //focus after first show
        LaunchedEffect(sheetState.currentValue) {
            if (sheetState.currentValue == SheetValue.Expanded ||
                sheetState.currentValue == SheetValue.PartiallyExpanded
            ) {
                focusRequester.requestFocus()
            }
        }
        //dismiss after hide keyboard
        LaunchedEffect(isKeyboardVisible) {
            if (wasKeyboardVisible && !isKeyboardVisible && sheetState.currentValue == SheetValue.Expanded) {
                scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss.invoke() }
            }
            wasKeyboardVisible = isKeyboardVisible
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            RoundedTextField(
                value = text,
                onValueChange = { newText -> text = newText },
                roundedCornerRes = R.dimen.rounded_corner,
                placeholderTextRes = placeholderResId,
                focusRequester,
                onDone = {
                    addItem.invoke(text)
                    text = ""
                         },
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(start = dimensionResource(id = R.dimen.root_padding), end = dimensionResource(id = R.dimen.root_padding), bottom = dimensionResource(id = R.dimen.inner_padding))
            )
        }
    }
}