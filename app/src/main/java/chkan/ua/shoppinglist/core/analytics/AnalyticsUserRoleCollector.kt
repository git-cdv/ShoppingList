package chkan.ua.shoppinglist.core.analytics


class AnalyticsUserRoleCollector(val complete: (String)->(Unit)) {

    private var roleState = RoleState()

    fun onPaidStatus(isPaid: Boolean) {
        roleState = roleState.copy(isPaid = isPaid)
        checkAndProcessState()
    }

    fun onInviteStatus(isInvited: Boolean) {
        roleState = roleState.copy(isInvited = isInvited)
        checkAndProcessState()
    }

    private fun checkAndProcessState() {
        if (roleState.isComplete()) {
            val result = when(roleState){
                RoleState(isPaid = true, isInvited = true) -> "invited_and_paid"
                RoleState(isPaid = true, isInvited = false) -> "paid"
                RoleState(isPaid = false, isInvited = true) -> "invited"
                else -> "user"
            }
            complete.invoke(result)
        }
    }
}

data class RoleState(
    var isPaid: Boolean? = null,
    var isInvited: Boolean? = null
) {
    fun isComplete(): Boolean {
        return isPaid != null && isInvited != null
    }
}