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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import chkan.ua.domain.models.HistoryItem
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.components.history_list.HistoryComponentState
import chkan.ua.shoppinglist.components.history_list.HistoryUiComponent
import chkan.ua.shoppinglist.core.components.StateDelegate
import chkan.ua.shoppinglist.ui.kit.RoundedTextField
import chkan.ua.shoppinglist.ui.screens.items.ItemsViewModel
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface StubHistoryComponent : StateDelegate<HistoryComponentState>

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddItemBottomSheet(
    sheetState: SheetState,
    listId: Int,
    onDismiss: () -> Unit,
    addItem: (String) -> Unit,
    placeholderResId: Int,
    itemsViewModel: ItemsViewModel = hiltViewModel()
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
            HistoryUiComponent(
                listId = listId,
                modifier = Modifier
                .fillMaxWidth()
                .padding(start = dimensionResource(id = R.dimen.root_padding), end = dimensionResource(id = R.dimen.root_padding), bottom = dimensionResource(id = R.dimen.inner_padding)),
                onChoose = { addItem.invoke(it) })

            RoundedTextField(
                text = text,
                onValueChange = { newText ->
                    text = newText
                    itemsViewModel.processAddItemBottomSheetChange(BottomSheetAction.SetText(newText))
                                },
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