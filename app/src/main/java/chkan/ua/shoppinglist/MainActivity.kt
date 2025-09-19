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
import chkan.ua.domain.Logger
import chkan.ua.shoppinglist.core.services.InviteHandler
import chkan.ua.shoppinglist.navigation.NavigationContainer
import chkan.ua.shoppinglist.session.SessionViewModel
import chkan.ua.shoppinglist.ui.kit.dialogs.ErrorDialogHandler
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

    @Inject
    lateinit var logger: Logger


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionViewModel.signInAnonymouslyIfNeed()
        sessionViewModel.handleInviteDataIfNeed(intent)
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
                        NavigationContainer(sessionViewModel, listsViewModel)
                        ErrorDialogHandler(listsViewModel)
                        InviteHandler(sessionViewModel, listsViewModel)
                        checkInstallReferrerIfNeed(sessionViewModel)
                        PaywallHandler(sessionViewModel,paywallViewModel)
                    }
                }
            }
        }
    }

    private fun checkInstallReferrerIfNeed(sessionViewModel: SessionViewModel) {
        if(sessionViewModel.isFirstLaunch){
            val referrerClient = InstallReferrerClient.newBuilder(this).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                        val response = referrerClient.installReferrer
                        val code = response.installReferrer
                        parseReferrerString(code, sessionViewModel)
                    }
                    referrerClient.endConnection()
                }

                override fun onInstallReferrerServiceDisconnected() {}
            })

        }
    }

    private fun parseReferrerString(referrerString: String, sessionViewModel: SessionViewModel) {
        try {
            logger.d("REFERRER","Raw referrer: $referrerString")

            val decodedReferrer = try {
                java.net.URLDecoder.decode(referrerString, "UTF-8")
            } catch (e: Exception) {
                referrerString
            }

            logger.d("REFERRER","Decoded referrer: $decodedReferrer")

            val uri = "?$decodedReferrer".toUri()
            val inviteCode = uri.getQueryParameter("invite_code")

            if (!inviteCode.isNullOrEmpty()) {
                logger.d("REFERRER","Found invite code: $inviteCode")
                sessionViewModel.setInviteCode(inviteCode)
            } else {
                logger.d("REFERRER","No invite_code parameter found")
            }
        } catch (e: Exception) {
            logger.e(e, "Error parsing referrer")
        }
    }

}