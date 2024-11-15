package chkan.ua.shoppinglist.core.services

import android.util.Log

interface ErrorHandler {
    fun handle(e:Exception, reason: String)
}

class ErrorHandlerBase : ErrorHandler {

    override fun handle(e: Exception, reason: String) {
        Log.d("CHKAN", "Error ${e.message} with reason: $reason")
    }

}