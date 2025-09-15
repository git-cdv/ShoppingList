package chkan.ua.core.exceptions

enum class ResourceCode {
    JOINING_LIST_NOT_FOUND,
    JOINING_USER_ALREADY_MEMBER,
    UNKNOWN_ERROR,
    NO_INTERNET_CONNECTION,
    SHARING_ERROR_CREATE_SHARED_LIST,
    SHARING_ERROR_STOP_SHARING_LIST,
}

data class UserMessageException(val resourceCode: ResourceCode, override val message: String = ""
) : Exception(message)