package chkan.ua.shoppinglist.ui.kit.bottom_sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import chkan.ua.domain.objects.Editable
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.kit.RoundedTextField
import chkan.ua.shoppinglist.ui.kit.RoundedTextFieldWithValue
import chkan.ua.shoppinglist.ui.screens.items.NoteTextField
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
    //add note
    var isAddNoteButtonShow by remember { mutableStateOf(editable.note.isNullOrBlank()) }
    var isAddNoteFieldShow by remember { mutableStateOf(!editable.note.isNullOrBlank()) }
    var textNote by rememberSaveable { mutableStateOf(editable.note) }
    val noteFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isAddNoteFieldShow) {
        if (isAddNoteFieldShow) {
            noteFocusRequester.requestFocus()
        } else {
            textNote = null
        }
    }

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
            RoundedTextFieldWithValue(
                value = value,
                onValueChange = { newValue -> value = newValue },
                roundedCornerRes = R.dimen.rounded_corner,
                focusRequester = focusRequester,
                maxLength = 50,
                onDone = {
                    if (value.text != editable.title || textNote != editable.note){
                        onEdit.invoke(editable.copy(title = value.text, note = textNote))
                    }
                    onDismiss.invoke()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(start = dimensionResource(id = R.dimen.root_padding), end = dimensionResource(id = R.dimen.root_padding))
            )

            if (isAddNoteButtonShow) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(
                            top = dimensionResource(id = R.dimen.inner_padding),
                            start = dimensionResource(id = R.dimen.root_padding),
                            bottom = dimensionResource(id = R.dimen.root_padding)
                        )
                        .clickable {
                            isAddNoteButtonShow = false
                            isAddNoteFieldShow = true
                        }
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        stringResource(R.string.add_note),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(if(isAddNoteFieldShow) 8.dp else 16.dp))
            }

            if (isAddNoteFieldShow) {
                NoteTextField(
                    text = textNote ?: "",
                    onValueChange = { textNote = it },
                    focusRequester = noteFocusRequester,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = if (!textNote.isNullOrBlank()) ImeAction.Done else ImeAction.None
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusRequester.requestFocus()
                            isAddNoteButtonShow = true
                            isAddNoteFieldShow = false
                        }
                    ),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = dimensionResource(id = R.dimen.root_padding)),
                    onClose = {
                        focusRequester.requestFocus()
                        isAddNoteButtonShow = true
                        isAddNoteFieldShow = false
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
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
            Editable("77", "Title")
        )
    }
}