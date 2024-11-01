package chkan.ua.shoppinglist.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController

val localNavController = staticCompositionLocalOf<NavController> {
    error("Didn't found NavController")
}