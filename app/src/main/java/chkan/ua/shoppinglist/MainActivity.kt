package chkan.ua.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import chkan.ua.shoppinglist.navigation.NavigationContainer
import chkan.ua.shoppinglist.ui.screens.lists.ListsExistResult
import chkan.ua.shoppinglist.ui.screens.lists.ListsViewModel
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val listsViewModel: ListsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                listsViewModel.isListsExist.value is ListsExistResult.Result
            }
        }
        enableEdgeToEdge()
        setContent {
            ShoppingListTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationContainer(innerPadding,listsViewModel)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartPreview() {
    ShoppingListTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavigationContainer(innerPadding)
        }
    }
}