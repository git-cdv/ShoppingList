package com.chkan.billing.domain.error

import kotlinx.coroutines.TimeoutCancellationException

class PurchasesException(
    val error: PurchasesError,
    override val message: String? = error.description,
) : Exception() {
    constructor(error: PurchasesError) : this(error, error.description)
}

enum class PurchasesError(val description: String) {
    NetworkError("Error performing request."),
    ProductNotAvailableForPurchaseError("Product is not available for purchase."),
    DeveloperError("Developer error."),
    UnknownError("Unknown error."),
    ProductAlreadyPurchasedError("This product is already active for the user."),
    PurchaseCancelledError("Purchase was cancelled."),
}

fun Throwable.toPurchasesException(): PurchasesException {
    return when (this) {
        is PurchasesException -> this

        is BillingException.ItemAlreadyOwnedException -> {
            PurchasesException(PurchasesError.ProductAlreadyPurchasedError, message)
        }

        is BillingException.ServiceDisconnectedException,
        is BillingException.ServiceUnavailableException,
        is BillingException.NetworkErrorException -> {
            PurchasesException(PurchasesError.NetworkError, message)
        }

        is BillingException.UserCanceledException -> {
            PurchasesException(PurchasesError.PurchaseCancelledError, message)
        }

        is BillingException.DeveloperErrorException -> {
            PurchasesException(PurchasesError.DeveloperError, message)
        }

        is TimeoutCancellationException -> {
            PurchasesException(PurchasesError.NetworkError, "Operation timed out")
        }

        is BillingException -> {
            PurchasesException(PurchasesError.UnknownError, message ?: "Billing error")
        }

        else -> {
            PurchasesException(
                PurchasesError.UnknownError,
                message ?: "Unknown error: ${this::class.simpleName}"
            )
        }
    }
}