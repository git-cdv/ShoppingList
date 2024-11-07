package chkan.ua.shoppinglist.core.services

import chkan.ua.core.interfaces.ErrorReasonGenerator

interface ErrorHandler {
    fun handle(e:Exception, reason: String)
}

class ErrorHandlerBase : ErrorHandler {

    override fun handle(e: Exception, reason: String) {

    }

}