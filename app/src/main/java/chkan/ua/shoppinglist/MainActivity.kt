package chkan.ua.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import chkan.ua.shoppinglist.navigation.NavigationContainer
import chkan.ua.shoppinglist.ui.screens.items.ItemsViewModel
import chkan.ua.shoppinglist.ui.screens.lists.ListsViewModel
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val listsViewModel: ListsViewModel by viewModels()
    private val itemsViewModel: ItemsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !listsViewModel.isLoadReady.value
            }
        }
        enableEdgeToEdge()
        setContent {
            ShoppingListTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (listsViewModel.isLoadReady.value) {
                        NavigationContainer()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartPreview() {
    ShoppingListTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            NavigationContainer()
        }
    }
}