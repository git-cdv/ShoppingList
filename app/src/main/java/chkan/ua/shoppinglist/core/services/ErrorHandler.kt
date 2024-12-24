package chkan.ua.shoppinglist.core.services

import android.util.Log
import javax.inject.Inject

interface ErrorHandler {
    fun handle(e:Exception, reason: String?)
}

class ErrorHandlerImpl @Inject constructor() : ErrorHandler {

    override fun handle(e: Exception, reason: String?) {
        Log.d("CHKAN", "Error ${e.message} with reason: $reason")
    }

}