package chkan.ua.shoppinglist.ui.screens.invite

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import chkan.ua.shoppinglist.navigation.FirstListRoute
import chkan.ua.shoppinglist.navigation.ListsRoute
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.InviteJoinBottomSheet
import kotlinx.coroutines.launch

sealed class InviteAction {
    object None : InviteAction()
    data class Joining(val code: String) : InviteAction()
    object Joined : InviteAction()
    object Error : InviteAction()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteHandler(
    inviteViewModel: InviteViewModel,
    navController: NavHostController
) {
    val inviteState by inviteViewModel.inviteState.collectAsStateWithLifecycle()
    var showInviteSheet by remember { mutableStateOf(false) }
    val inviteSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(inviteState) {
        if (inviteState is InviteAction.Joining) {
            showInviteSheet = true
            scope.launch { inviteSheetState.show() }
        }
        if (inviteState is InviteAction.Joined) {
            if(navController.currentBackStackEntry?.destination?.route?.contains("FirstListRoute") == true) {
                navController.navigate(ListsRoute){
                    popUpTo(FirstListRoute) { inclusive = true }
                }
            }
        }
    }

    if (showInviteSheet && inviteState is InviteAction.Joining) {
        InviteJoinBottomSheet(
            sheetState = inviteSheetState,
            onJoin = {
                scope.launch {
                    inviteViewModel.onJoinList((inviteState as? InviteAction.Joining)?.code)
                    inviteSheetState.hide()
                    showInviteSheet = false
                    inviteViewModel.clearInviteData()
                }
            },
            onDismiss = {
                scope.launch {
                    inviteSheetState.hide()
                    showInviteSheet = false
                    inviteViewModel.clearInviteData()
                }
            }
        )
    }
}
