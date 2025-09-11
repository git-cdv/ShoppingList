package com.chkan.billing.domain.model

enum class BillingError(val code: Int, val description: String) {
    SERVICE_TIMEOUT(-3, "The request has reached the maximum timeout before Google Play responds"),
    FEATURE_NOT_SUPPORTED(-2, "Requested feature is not supported by Play Store"),
    SERVICE_DISCONNECTED(-1, "Play Store service is not connected now"),
    OK(0, "Success"),
    USER_CANCELED(1, "User pressed back or canceled a dialog"),
    SERVICE_UNAVAILABLE(2, "Network connection is down"),
    BILLING_UNAVAILABLE(3, "Billing API version is not supported for the type requested"),
    ITEM_UNAVAILABLE(4, "Requested product is not available for purchase"),
    DEVELOPER_ERROR(5, "Invalid arguments provided to the API"),
    ERROR(6, "Fatal error during the API action"),
    ITEM_ALREADY_OWNED(7, "Failure to purchase since item is already owned"),
    ITEM_NOT_OWNED(8, "Failure to consume since item is not owned")
}
