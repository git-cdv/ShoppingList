package chkan.ua.shoppinglist.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import chkan.ua.shoppinglist.session.SessionViewModel
import chkan.ua.shoppinglist.ui.screens.first_list.FirstListScreen
import chkan.ua.shoppinglist.ui.screens.invite.InviteViewModel
import chkan.ua.shoppinglist.ui.screens.items.ItemsScreen
import chkan.ua.shoppinglist.ui.screens.lists.ListsScreen
import chkan.ua.shoppinglist.ui.screens.lists.ListsViewModel

@Composable
fun NavigationContainer(
    navController: NavHostController,
    sessionViewModel: SessionViewModel,
    listsViewModel: ListsViewModel,
    inviteViewModel: InviteViewModel
) {
    val startDestination: Any = if (sessionViewModel.isFirstLaunch) FirstListRoute else ListsRoute

    CompositionLocalProvider(
        localNavController provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize()
        ) {
            composable<FirstListRoute> { FirstListScreen(listsViewModel) }
            composable<ItemsRoute>(
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }) { backStackEntry ->
                val args: ItemsRoute = backStackEntry.toRoute()
                ItemsScreen(
                    args = args,
                    sessionViewModel = sessionViewModel,
                    listsViewModel =listsViewModel
                )
            }
            composable<ListsRoute> { ListsScreen(sessionViewModel,listsViewModel) }
        }

        if (startDestination is ListsRoute && !inviteViewModel.isLaunchWithInvite()){
            val lastOpenedList = sessionViewModel.getLastOpenedList()
            if (lastOpenedList != null){
                navController.navigate(ItemsRoute(lastOpenedList.id,lastOpenedList.title, lastOpenedList.role)){
                    popUpTo(ListsRoute) { inclusive = false }
                }
            }
        }
    }
}