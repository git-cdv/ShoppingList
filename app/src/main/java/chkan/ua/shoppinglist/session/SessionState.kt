package chkan.ua.shoppinglist.session

import androidx.compose.runtime.Immutable

@Immutable
data class SessionState(
    val isSubscribed: Boolean? = null,
    val isInvited: Boolean = false
)
