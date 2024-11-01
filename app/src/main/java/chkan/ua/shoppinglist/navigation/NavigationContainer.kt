package chkan.ua.shoppinglist.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import chkan.ua.shoppinglist.ui.screens.first_list.FirstListScreen
import chkan.ua.shoppinglist.ui.screens.items.ItemsScreen

@Composable
fun NavigationContainer(innerPadding: PaddingValues) {
    val navController = rememberNavController()

    CompositionLocalProvider(
        localNavController provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = FirstListRoute,
            modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            composable(FirstListRoute) { FirstListScreen() }
            composable(ItemsRoute) { ItemsScreen() }
        }
    }
}