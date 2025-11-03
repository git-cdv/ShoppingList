package chkan.ua.shoppinglist.ui.kit.bottom_sheets

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeNestedScroll
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.components.history_list.HistoryComponentState
import chkan.ua.shoppinglist.components.history_list.HistoryUiComponent
import chkan.ua.shoppinglist.core.components.StateDelegate
import chkan.ua.shoppinglist.ui.kit.AddItemContainer
import chkan.ua.shoppinglist.ui.screens.items.ItemsViewModel
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface StubHistoryComponent : StateDelegate<HistoryComponentState>

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddItemBottomSheet(
    sheetState: SheetState,
    listId: String,
    onDismiss: () -> Unit,
    addItem: (AddedItem) -> Unit,
    itemsViewModel: ItemsViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var text by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    var wasKeyboardVisible by remember { mutableStateOf(false) }
    val isKeyboardVisible = WindowInsets.isImeVisible

    // Добавляем задержку для плавного закрытия
    var dismissAfterKeyboardHide by remember { mutableStateOf(false) }

    //add note
    var textNote by rememberSaveable { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismiss.invoke()
                    focusRequester.freeFocus()
                }
            }
        },
        dragHandle = null
    )
    {
        //focus after first show
        LaunchedEffect(sheetState.currentValue) {
            if (sheetState.currentValue == SheetValue.Expanded ||
                sheetState.currentValue == SheetValue.PartiallyExpanded
            ) {
                delay(300)
                focusRequester.requestFocus()
            }
        }
        //dismiss after hide keyboard
        LaunchedEffect(isKeyboardVisible, dismissAfterKeyboardHide) {
            if (wasKeyboardVisible && !isKeyboardVisible &&
                sheetState.currentValue == SheetValue.Expanded) {

                // Проверяем, есть ли контент или активное взаимодействие
                if (text.isBlank() && textNote.isNullOrBlank()) {
                    dismissAfterKeyboardHide = true
                    // Добавляем небольшую задержку для плавности
                    delay(150)
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        onDismiss.invoke()
                    }
                }
            }
            wasKeyboardVisible = isKeyboardVisible

            // Сбрасываем флаг если клавиатура появилась снова
            if (isKeyboardVisible) {
                dismissAfterKeyboardHide = false
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .imeNestedScroll()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
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

            AddItemContainer(
                itemText = text,
                noteText = textNote ?: "",
                onItemChange = { newText ->
                    text = newText
                    itemsViewModel.processAddItemBottomSheetChange(BottomSheetAction.SetText(newText))
                },
                onNoteChange = { textNote = it },
                focusRequester = focusRequester,
                onDone = {
                    if (text.isNotBlank()) {
                        addItem.invoke(AddedItem(text.trim(), textNote?.trim()))
                        focusRequester.requestFocus()
                        text = ""
                        textNote = null
                    }
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
            listId = "2",
            onDismiss = {},
            addItem = {}
        )
    }
}