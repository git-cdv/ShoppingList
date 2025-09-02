package chkan.ua.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import chkan.ua.shoppinglist.navigation.NavigationContainer
import chkan.ua.shoppinglist.session.SessionViewModel
import chkan.ua.shoppinglist.ui.kit.dialogs.ErrorDialogHandler
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val sessionViewModel: SessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionViewModel.signInAnonymouslyIfNeed()
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !sessionViewModel.isLoadReady.value
            }
        }
        enableEdgeToEdge()
        setContent {
            ShoppingListTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (sessionViewModel.isLoadReady.value) {
                        NavigationContainer(sessionViewModel)
                        ErrorDialogHandler()
                    }
                }
            }
        }
    }
}