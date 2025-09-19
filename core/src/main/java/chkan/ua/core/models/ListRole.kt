package chkan.ua.core.models

enum class ListRole { LOCAL, SHARED_OWNER, SHARED_MEMBER }

val ListRole.isShared: Boolean
    get() = this != ListRole.LOCAL
