package chkan.ua.shoppinglist

import android.net.Uri
import android.os.Bundle
import android.util.Log
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

        val appLinkIntent = intent
        val appLinkData: Uri? = appLinkIntent.data

        appLinkData?.let { uri ->
            Log.d("DeepLink", "uri: $uri")
            val inviteCode = uri.getQueryParameter("code")
            Log.d("DeepLink", "Invite code: $inviteCode")
        }

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