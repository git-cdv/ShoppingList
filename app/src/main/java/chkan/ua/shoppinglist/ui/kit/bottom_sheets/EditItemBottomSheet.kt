package chkan.ua.shoppinglist.ui.kit.bottom_sheets

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import chkan.ua.domain.objects.Editable
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.kit.EditItemContainer
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditItemBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onEdit: (Editable) -> Unit,
    editable: Editable)
{
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
    //add note
    var textNote by rememberSaveable { mutableStateOf(editable.note) }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {  scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismiss.invoke()
                focusRequester.freeFocus()
            }
        }},
        dragHandle = null)
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

        Spacer(modifier = Modifier.height(16.dp))

        EditItemContainer(
            itemText = value,
            noteText = textNote ?: "",
            onItemChange = { newValue -> value = newValue },
            onNoteChange = { textNote = it },
            focusRequester = focusRequester,
            onDone = {
                if (value.text != editable.title || textNote != editable.note){
                    onEdit.invoke(editable.copy(title = value.text, note = textNote))
                }
                onDismiss.invoke()
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusable()
                .padding(
                    start = dimensionResource(id = R.dimen.root_padding),
                    end = dimensionResource(id = R.dimen.root_padding)
                ),
            maxLength = 100
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun EditItemBottomSheetPreview() {
    ShoppingListTheme {
        EditItemBottomSheet(
            sheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Expanded
            ),
            onDismiss = {},
            onEdit = {},
            Editable("77", "Title")
        )
    }
}