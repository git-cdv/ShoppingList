package chkan.ua.core.exceptions

enum class ResourceCode {
    JOINING_LIST_NOT_FOUND,
    JOINING_USER_ALREADY_MEMBER,
    UNKNOWN_ERROR,
    NO_INTERNET_CONNECTION
}

data class UserMessageException(val resourceCode: ResourceCode, override val message: String = ""
) : Exception(message)