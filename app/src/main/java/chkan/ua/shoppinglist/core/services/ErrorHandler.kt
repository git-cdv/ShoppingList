package chkan.ua.shoppinglist.core.services

interface ErrorHandler {
    fun handle(e:Exception, reason: String)
}

class ErrorHandlerBase : ErrorHandler {

    override fun handle(e: Exception, reason: String) {

    }

}