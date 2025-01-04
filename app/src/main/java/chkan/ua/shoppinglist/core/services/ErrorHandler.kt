package chkan.ua.shoppinglist.core.services

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

interface ErrorHandler {
    fun handle(e:Exception, reason: String?)
    val errorFlow: SharedFlow<ErrorEvent>
}

class ErrorHandlerImpl @Inject constructor() : ErrorHandler {

    private val _errorFlow = MutableSharedFlow<ErrorEvent>(replay = 1)
    override val errorFlow = _errorFlow.asSharedFlow()

    override fun handle(e: Exception, reason: String?) {
        Log.d("CHKAN", "Error ${e.message} with reason: $reason")
        val result = _errorFlow.tryEmit(ErrorEvent(e, reason))
        Log.d("CHKAN", "Error push in flow - $result")
    }

}

data class ErrorEvent(val e: Exception, val reason: String?)