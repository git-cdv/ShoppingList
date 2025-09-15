package chkan.ua.shoppinglist.session

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chkan.ua.domain.Logger
import chkan.ua.domain.objects.LastOpenedList
import chkan.ua.domain.usecases.auth.SignInAnonymouslyUseCase
import chkan.ua.shoppinglist.core.remoteconfigs.RemoteConfigManager
import chkan.ua.shoppinglist.core.services.SharedPreferencesService
import chkan.ua.shoppinglist.core.services.SharedPreferencesServiceImpl.Companion.IS_FIRST_LAUNCH
import chkan.ua.shoppinglist.core.services.SharedPreferencesServiceImpl.Companion.LAST_OPEN_LIST_ID_INT
import chkan.ua.shoppinglist.core.services.SharedPreferencesServiceImpl.Companion.LAST_OPEN_LIST_IS_SHARED
import chkan.ua.shoppinglist.core.services.SharedPreferencesServiceImpl.Companion.LAST_OPEN_LIST_TITLE_STR
import chkan.ua.shoppinglist.ui.screens.paywall.PaywallCollector
import com.chkan.billing.service.SubscriptionState
import com.chkan.billing.service.SubscriptionStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
    private val spService: SharedPreferencesService,
    private val subscriptionStateManager: SubscriptionStateManager,
    private val paywallCollector: PaywallCollector,
    private val logger: Logger
) : ViewModel() {

    private val _sessionState = MutableStateFlow(SessionState())
    val sessionState = _sessionState.asStateFlow()

    init {
        checkFirstLaunch()
        observeIsSubscribed()
    }

    private val _inviteCode = MutableStateFlow<String?>(null)
    val inviteCode = _inviteCode.asStateFlow()
    var isFirstLaunch = false

    private val _isLoadReady = mutableStateOf(false)
    val isLoadReady: State<Boolean> = _isLoadReady

    private fun checkFirstLaunch() {
        viewModelScope.launch(Dispatchers.IO) {
            val splashMinShowTime = 200L

            val dataJob = launch {
                isFirstLaunch = spService.get(IS_FIRST_LAUNCH, Boolean::class.java) ?: true
                if (isFirstLaunch) {
                    spService.set(IS_FIRST_LAUNCH, false)
                }
            }

            delay(splashMinShowTime)
            dataJob.join()
            _isLoadReady.value = true
        }
    }

    private fun observeIsSubscribed() {
        viewModelScope.launch {
            subscriptionStateManager.subscriptionState.collect { state ->
                Timber.tag("SESSION_VM").d("subscriptionState: $state")
                when (state) {
                    SubscriptionState.Active -> { _sessionState.update { it.copy(isSubscribed = true) } }
                    SubscriptionState.Inactive -> {
                        _sessionState.update { it.copy(isSubscribed = false) }
                        paywallCollector.init()
                    }
                    SubscriptionState.Loading -> {}
                }
            }
        }
    }

    fun signInAnonymouslyIfNeed() {
        viewModelScope.launch {
            signInAnonymouslyUseCase()
        }
    }

    fun clearLastOpenedList() {
        spService.set(LAST_OPEN_LIST_ID_INT, 0)
        spService.set(LAST_OPEN_LIST_TITLE_STR, "")
        spService.set(LAST_OPEN_LIST_IS_SHARED, false)
    }

    fun getLastOpenedList(): LastOpenedList? {
        return try {
            val id = spService.get(LAST_OPEN_LIST_ID_INT, String::class.java) ?: ""
            val title = spService.get(LAST_OPEN_LIST_TITLE_STR, String::class.java) ?: ""
            val isShared = spService.get(LAST_OPEN_LIST_IS_SHARED, Boolean::class.java) ?: false
            LastOpenedList(id, title, isShared)
        } catch (e: Exception) {
            null
        }
    }

    fun handleInviteDataIfNeed(intent: Intent) {
        val appLinkIntent = intent
        val appLinkData: Uri? = appLinkIntent.data

        appLinkData?.let { uri ->
            try {
                val inviteCode = uri.getQueryParameter("code")
                val listId = inviteCode?.drop(2)
                _inviteCode.update { listId }
            } catch (e: Exception) {
                logger.e(e, "Error while parsing invite code: $uri")
            }
        }
    }

    fun clearInviteData() {
        _inviteCode.update { null }
    }

    fun isLaunchWithInvite(): Boolean {
        return _inviteCode.value != null
    }

    fun setInviteCode(code: String) {
        Timber.tag("SESSION_VM").d("setInviteCode: $code")
        _inviteCode.update { code.drop(2) }
    }
}