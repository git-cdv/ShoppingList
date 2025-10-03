package chkan.ua.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import chkan.ua.domain.Logger
import chkan.ua.domain.Analytics
import chkan.ua.shoppinglist.ui.screens.invite.InviteHandler
import chkan.ua.shoppinglist.navigation.NavigationContainer
import chkan.ua.shoppinglist.session.SessionViewModel
import chkan.ua.shoppinglist.ui.kit.dialogs.ErrorDialogHandler
import chkan.ua.shoppinglist.ui.screens.invite.InviteViewModel
import chkan.ua.shoppinglist.ui.screens.lists.ListsViewModel
import chkan.ua.shoppinglist.ui.screens.paywall.PaywallHandler
import chkan.ua.shoppinglist.ui.screens.paywall.data.PaywallViewModel
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val sessionViewModel: SessionViewModel by viewModels()
    private val listsViewModel: ListsViewModel by viewModels()
    private val paywallViewModel: PaywallViewModel by viewModels()
    private val inviteViewModel: InviteViewModel by viewModels()

    @Inject
    lateinit var logger: Logger

    @Inject
    lateinit var analytics: Analytics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionViewModel.signInAnonymouslyIfNeed()
        inviteViewModel.handleInviteDataIfNeed(intent)
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
                        val navController = rememberNavController()
                        NavigationContainer(navController, analytics, sessionViewModel, listsViewModel, inviteViewModel)
                        ErrorDialogHandler(listsViewModel)
                        InviteHandler(inviteViewModel,navController)
                        checkInstallReferrerIfNeed()
                        PaywallHandler(sessionViewModel,paywallViewModel)
                    }
                }
            }
        }
    }

    private fun checkInstallReferrerIfNeed() {
        if(sessionViewModel.isFirstLaunch){
            val referrerClient = InstallReferrerClient.newBuilder(this).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                        val response = referrerClient.installReferrer
                        val code = response.installReferrer
                        parseReferrerString(code)
                    }
                    referrerClient.endConnection()
                }

                override fun onInstallReferrerServiceDisconnected() {}
            })

        }
    }

    private fun parseReferrerString(referrerString: String) {
        try {
            logger.d("REFERRER","Raw referrer: $referrerString")

            val decodedReferrer = try {
                java.net.URLDecoder.decode(referrerString, "UTF-8")
            } catch (e: Exception) {
                referrerString
            }

            logger.d("REFERRER","Decoded referrer: $decodedReferrer")

            val uri = "?$decodedReferrer".toUri()
            val inviteCode = uri.getQueryParameter("code")

            if (!inviteCode.isNullOrEmpty()) {
                logger.d("REFERRER","Found invite code: $inviteCode")
                inviteViewModel.setInviteCode(inviteCode)
            } else {
                logger.d("REFERRER","No invite_code parameter found")
            }
        } catch (e: Exception) {
            logger.e(e, "Error parsing referrer")
        }
    }

}