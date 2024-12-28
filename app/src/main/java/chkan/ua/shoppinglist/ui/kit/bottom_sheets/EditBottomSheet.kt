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
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import chkan.ua.domain.objects.Editable
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.kit.RoundedTextField
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onEdit: (Editable) -> Unit,
    editable: Editable
    ){
    val scope = rememberCoroutineScope()
    val textFieldValueSaver = remember {
        Saver<TextFieldValue, Pair<String, TextRange>>(
            save = { value -> value.text to value.selection },
            restore = { TextFieldValue(it.first, it.second) }
        )
    }

    var value by rememberSaveable(stateSaver = textFieldValueSaver) {
        mutableStateOf(TextFieldValue(""))
    }
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
        LaunchedEffect(Unit) {
            value = TextFieldValue(
                text = editable.title,
                selection = TextRange(editable.title.length)
            )
        }

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
                value = value,
                onValueChange = { newValue -> value = newValue },
                roundedCornerRes = R.dimen.rounded_corner,
                focusRequester = focusRequester,
                onDone = {
                    if (value.text != editable.title){
                        onEdit.invoke(editable.copy(title = value.text))
                    }
                    onDismiss.invoke()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(start = dimensionResource(id = R.dimen.root_padding), end = dimensionResource(id = R.dimen.root_padding), bottom = dimensionResource(id = R.dimen.inner_padding))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun EditBottomSheetPreview() {
    ShoppingListTheme {
        EditBottomSheet(
            sheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Expanded
            ),
            onDismiss = {},
            onEdit = {},
            Editable(77, "Title")
        )
    }
}