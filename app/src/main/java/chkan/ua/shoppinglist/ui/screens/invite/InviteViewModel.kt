package chkan.ua.shoppinglist.ui.screens.invite

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chkan.ua.domain.Analytics
import chkan.ua.domain.Logger
import chkan.ua.domain.usecases.session.IsInvitedUseCase
import chkan.ua.domain.usecases.share.HasSharedListsUseCase
import chkan.ua.domain.usecases.share.JoinListUseCase
import chkan.ua.shoppinglist.core.services.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class InviteViewModel @Inject constructor(
    private val joinList: JoinListUseCase,
    private val hasSharedListsUseCase: HasSharedListsUseCase,
    private val isInvitedUseCase: IsInvitedUseCase,
    private val errorHandler: ErrorHandler,
    private val logger: Logger,
    private val analytics: Analytics,
) : ViewModel() {
    private val _inviteState = MutableStateFlow<InviteAction>(InviteAction.None)
    val inviteState = _inviteState.asStateFlow()

    fun onJoinList(inviteCode: String?) {
        inviteCode?.let { code ->
            viewModelScope.launch(Dispatchers.IO) {
                joinList(code)
                    .onSuccess {
                        hasSharedListsUseCase.setState(true)
                        _inviteState.update { InviteAction.Joined }
                        isInvitedUseCase.set(true)
                    }
                    .onFailure {
                        errorHandler.handle(it, it.message)
                        _inviteState.update { InviteAction.Error }
                        val errorInfo = mapOf(
                            "error" to it.javaClass.simpleName,
                            "error_message" to (it.message?.take(100) ?: "No message")
                        )
                        analytics.logEvent("invite_join_error",errorInfo)
                    }
            }
        }
    }

    fun handleInviteDataIfNeed(intent: Intent) {

        if (intent.getBooleanExtra("invite_processed", false)) {
            return
        }

        val appLinkIntent = intent
        val appLinkData: Uri? = appLinkIntent.data

        appLinkData?.let { uri ->
            try {
                val inviteCode = uri.getQueryParameter("code")
                val listId = inviteCode?.drop(2)
                if (!listId.isNullOrEmpty()) {
                    _inviteState.update { InviteAction.Joining(listId) }
                    analytics.logEvent("invite_success",mapOf("code" to inviteCode,"source" to "appLink"))
                }
                intent.putExtra("invite_processed", true)
            } catch (e: Exception) {
                _inviteState.update { InviteAction.Error }
                logger.e(e, "Error while parsing invite code: $uri")
                analytics.logEvent("invite_error",mapOf("message" to "Error while parsing: $uri"))
            }
        }
    }

    fun clearInviteData() {
        _inviteState.update { InviteAction.None }
    }

    fun isLaunchWithInvite(): Boolean {
        return _inviteState.value is InviteAction.Joining
    }

    fun setInviteCode(code: String) {
        logger.d("SESSION_VM","setInviteCode: $code")
        _inviteState.update { InviteAction.Joining(code.drop(2)) }
        analytics.logEvent("invite_success",mapOf("code" to code,"source" to "referrer"))
    }

}