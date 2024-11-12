package chkan.ua.shoppinglist.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import chkan.ua.shoppinglist.ui.screens.first_list.FirstListScreen
import chkan.ua.shoppinglist.ui.screens.items.ItemsScreen
import chkan.ua.shoppinglist.ui.screens.lists.ListsScreen
import chkan.ua.shoppinglist.ui.screens.lists.ListsViewModel

@Composable
fun NavigationContainer(
    innerPadding: PaddingValues,
    listsViewModel: ListsViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val startDestination: Any = if (listsViewModel.isListExist()) ListsRoute else FirstListRoute

    CompositionLocalProvider(
        localNavController provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)) {
            composable<FirstListRoute> { FirstListScreen() }
            composable<ItemsRoute> { backStackEntry ->
                val args: ItemsRoute = backStackEntry.toRoute()
                ItemsScreen(args)
            }
            composable<ListsRoute> { ListsScreen() }
        }
    }
}