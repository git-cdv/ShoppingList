package chkan.ua.shoppinglist.core.services

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chkan.ua.shoppinglist.session.SessionViewModel
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.InviteJoinBottomSheet
import chkan.ua.shoppinglist.ui.screens.lists.ListsViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteHandler(
    sessionViewModel: SessionViewModel,
    listsViewModel: ListsViewModel = hiltViewModel()
) {
    val inviteCode by sessionViewModel.inviteCode.collectAsStateWithLifecycle()
    var showInviteSheet by remember { mutableStateOf(false) }
    val inviteSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(inviteCode) {
        if (inviteCode != null) {
            showInviteSheet = true
            scope.launch { inviteSheetState.show() }
        }
    }

    if (showInviteSheet && inviteCode != null) {
        InviteJoinBottomSheet(
            sheetState = inviteSheetState,
            onJoin = {
                scope.launch {
                    listsViewModel.onJoinList(inviteCode)
                    inviteSheetState.hide()
                    showInviteSheet = false
                    sessionViewModel.clearInviteData()
                }
            },
            onDismiss = {
                scope.launch {
                    inviteSheetState.hide()
                    showInviteSheet = false
                    sessionViewModel.clearInviteData()
                }
            }
        )
    }
}
