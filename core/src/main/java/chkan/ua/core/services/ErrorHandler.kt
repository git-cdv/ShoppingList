package chkan.ua.core.services

import chkan.ua.core.interfaces.ErrorReasonGenerator

interface ErrorHandler {
    fun handle(e:Exception, reason: ErrorReasonGenerator)
}

class ErrorHandlerBase : ErrorHandler {

    override fun handle(e: Exception, reason: ErrorReasonGenerator) {

    }

}