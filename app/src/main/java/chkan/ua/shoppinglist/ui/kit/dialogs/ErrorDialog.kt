package chkan.ua.shoppinglist.ui.kit.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.screens.lists.ListsViewModel

@Composable
fun ErrorDialogHandler(
    listsViewModel: ListsViewModel
) {

    val handler = listsViewModel.errorHandler
    val currentErrorMessage = rememberSaveable{ mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        handler.errorChannelFlow.collect { errorMessage ->
            currentErrorMessage.value = errorMessage
        }
    }

    if (currentErrorMessage.value != null) {
        AlertDialog(
            onDismissRequest = { currentErrorMessage.value = null },
            confirmButton = {
                TextButton(onClick = { currentErrorMessage.value = null }) {
                    Text("OK")
                }
            },
            title = { Text(stringResource(id = R.string.error)) },
            text = {
                Text(currentErrorMessage.value ?: stringResource(R.string.working_on_solution))
            }
        )
    }
}