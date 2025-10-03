package chkan.ua.core.models

enum class ListRole { LOCAL, SHARED_OWNER, SHARED_MEMBER }

val ListRole.isShared: Boolean
    get() = this != ListRole.LOCAL

fun ListRole.toPreferenceString(): String = this.name

fun String.toListRole(): ListRole = try {
    ListRole.valueOf(this)
} catch (e: Exception) {
    ListRole.LOCAL
}

