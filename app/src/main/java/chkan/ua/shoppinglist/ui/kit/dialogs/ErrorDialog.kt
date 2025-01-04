package chkan.ua.shoppinglist.ui.kit.dialogs

import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import chkan.ua.shoppinglist.core.services.ErrorEvent
import chkan.ua.shoppinglist.ui.screens.lists.ListsViewModel

@Composable
fun ErrorDialogHandler(
    listsViewModel: ListsViewModel = hiltViewModel()
) {
    val handler = listsViewModel.errorHandler
    var currentError by rememberSaveable { mutableStateOf<ErrorEvent?>(null) }

    LaunchedEffect(Unit) {
        Log.d("CHKAN", "errorHandler IN ErrorDialogHandler ${handler.hashCode()}")
        handler.errorFlow.collect { errorEvent ->
            Log.d("CHKAN", "errorFlow $errorEvent")
            currentError = errorEvent
        }
    }

    if (currentError != null) {
        AlertDialog(
            onDismissRequest = { currentError = null },
            confirmButton = {
                TextButton(onClick = { currentError = null }) {
                    Text("OK")
                }
            },
            title = { Text("Error") },
            text = {
                Text(
                    "An error occurred: ${currentError?.e?.message}. " +
                            "Reason: ${currentError?.reason ?: "Unknown"}"
                )
            }
        )
    }
}