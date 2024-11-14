package chkan.ua.shoppinglist.ui.kit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import chkan.ua.shoppinglist.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemBottomSheet(sheetState: SheetState,onDismiss: () -> Unit, addItem: (String) -> Unit ){
    val scope = rememberCoroutineScope()
    var text by rememberSaveable { mutableStateOf("") }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {  scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismiss.invoke()
            }
        }})
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            RoundedTextField(
                value = text,
                onValueChange = { newText -> text = newText },
                roundedCornerRes = R.dimen.rounded_corner,
                placeholderTextRes = R.string.first_list_text_placeholder,
                onDone = { addItem.invoke(text) },
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(horizontal = dimensionResource(id = R.dimen.root_padding))
            )
        }
    }
}