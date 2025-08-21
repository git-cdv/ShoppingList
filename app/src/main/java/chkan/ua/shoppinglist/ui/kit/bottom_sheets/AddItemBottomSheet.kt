package chkan.ua.shoppinglist.ui.kit.bottom_sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.components.history_list.HistoryComponentState
import chkan.ua.shoppinglist.components.history_list.HistoryUiComponent
import chkan.ua.shoppinglist.core.components.StateDelegate
import chkan.ua.shoppinglist.ui.kit.RoundedTextField
import chkan.ua.shoppinglist.ui.screens.items.ItemsViewModel
import chkan.ua.shoppinglist.ui.screens.items.NoteTextField
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.launch

interface StubHistoryComponent : StateDelegate<HistoryComponentState>

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddItemBottomSheet(
    sheetState: SheetState,
    listId: Int,
    onDismiss: () -> Unit,
    addItem: (AddedItem) -> Unit,
    placeholderResId: Int,
    itemsViewModel: ItemsViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var text by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    var wasKeyboardVisible by remember { mutableStateOf(false) }
    val isKeyboardVisible = WindowInsets.isImeVisible
    //add note
    var isAddNoteButtonShow by remember { mutableStateOf(false) }
    var isAddNoteFieldShow by remember { mutableStateOf(false) }
    var textNote by rememberSaveable { mutableStateOf<String?>(null) }
    val noteFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isAddNoteFieldShow) {
        if (isAddNoteFieldShow) {
            noteFocusRequester.requestFocus()
        } else {
            textNote = ""
        }
    }


    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismiss.invoke()
                    focusRequester.freeFocus()
                }
            }
        })
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
            HistoryUiComponent(
                listId = listId,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensionResource(id = R.dimen.root_padding),
                        end = dimensionResource(id = R.dimen.root_padding),
                        bottom = dimensionResource(id = R.dimen.inner_padding)
                    ),
                onChoose = { addItem.invoke(AddedItem(it, textNote)) })

            RoundedTextField(
                text = text,
                onValueChange = { newText ->
                    text = newText
                    itemsViewModel.processAddItemBottomSheetChange(BottomSheetAction.SetText(newText))
                    isAddNoteButtonShow = newText.isNotBlank() && !isAddNoteFieldShow
                    if(newText.isBlank()) {
                        isAddNoteFieldShow = false
                    }
                },
                roundedCornerRes = R.dimen.rounded_corner,
                placeholderTextRes = placeholderResId,
                focusRequester,
                onDone = {
                    if (text.isNotBlank()) {
                        addItem.invoke(AddedItem(text, textNote))
                        focusRequester.requestFocus()
                        isAddNoteButtonShow = false
                        isAddNoteFieldShow = false
                        text = ""
                    }
                },
                maxLength = 50,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .focusable()
                    .padding(
                        start = dimensionResource(id = R.dimen.root_padding),
                        end = dimensionResource(id = R.dimen.root_padding)
                    )
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

data class AddedItem(val content: String, val note: String? = null)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AddItemBottomSheetPreview() {
    ShoppingListTheme {
        AddItemBottomSheet(
            sheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Expanded
            ),
            listId = 2,
            onDismiss = {},
            addItem = {},
            placeholderResId = R.string.items_text_placeholder
        )
    }
}