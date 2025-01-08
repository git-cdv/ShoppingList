package chkan.ua.shoppinglist.ui.kit.dialogs

import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.core.services.ErrorEvent
import chkan.ua.shoppinglist.ui.screens.lists.ListsViewModel

@Composable
fun ErrorDialogHandler(
    listsViewModel: ListsViewModel = hiltViewModel()
) {

    val errorEventSaver = Saver<MutableState<ErrorEvent?>, List<String?>>(
        save = { state ->
            listOf(state.value?.exType, state.value?.exMessage, state.value?.reason)
        },
        restore = { savedData ->
            savedData[0]?.let {
                mutableStateOf(ErrorEvent(
                    exType = savedData[0] ?: "",
                    exMessage = savedData[1] ?: "",
                    reason = savedData[2] ?: ""
                ))
            } ?: mutableStateOf(null)
        }
    )

    val handler = listsViewModel.errorHandler
    val currentError = rememberSaveable(saver = errorEventSaver) { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        handler.errorChannelFlow.collect { errorEvent ->
            currentError.value = errorEvent
        }
    }

    if (currentError.value != null) {
        AlertDialog(
            onDismissRequest = { currentError.value = null },
            confirmButton = {
                TextButton(onClick = { currentError.value = null }) {
                    Text("OK")
                }
            },
            title = { Text(stringResource(id = R.string.error)) },
            text = {
                Text(
                            stringResource(id = R.string.reason) + ": ${currentError.value?.reason ?: stringResource(id = R.string.unknown)}. " +
                                    stringResource(id = R.string.working_on_solution)
                )
            }
        )
    }
}