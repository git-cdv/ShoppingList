package chkan.ua.shoppinglist.core.services

import android.util.Log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

interface ErrorHandler {
    suspend fun handle(e:Exception, reason: String)
    val errorChannelFlow: Flow<ErrorEvent>
}

class ErrorHandlerImpl @Inject constructor() : ErrorHandler {

    private val errorChannel = Channel<ErrorEvent>()
    override val errorChannelFlow = errorChannel.receiveAsFlow()

    override suspend fun handle(e: Exception, reason: String) {
        Log.d("CHKAN", "Error ${e.message} with reason: $reason")
        errorChannel.send(ErrorEvent(e::class.simpleName ?: "", e.message ?: "", reason))
    }

}

data class ErrorEvent(val exType: String, val exMessage: String, val reason: String)