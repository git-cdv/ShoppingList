package chkan.ua.shoppinglist.core.services

import android.content.Context
import chkan.ua.core.exceptions.ResourceCode
import chkan.ua.core.exceptions.UserMessageException
import chkan.ua.domain.Logger
import chkan.ua.shoppinglist.BuildConfig
import chkan.ua.shoppinglist.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface ErrorHandler {
    fun handle(e: Throwable, reason: String? = null)
    val errorChannelFlow: Flow<String>
}

@Singleton
class ErrorHandlerImpl @Inject constructor(
    private val logger: Logger,
    @ApplicationContext private val context: Context,
) : ErrorHandler {

    private val errorChannel = Channel<String>()
    override val errorChannelFlow = errorChannel.receiveAsFlow()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun handle(e: Throwable, reason: String?) {
        logger.e(e, reason)
        scope.launch {
            if(BuildConfig.DEBUG){
                if(e is UserMessageException){
                    val message = getLocalizeMessage(e.resourceCode)
                    errorChannel.send(message)
                } else {
                    errorChannel.send(reason ?: e.message ?: "Unknown error")
                }
            } else {
                if(e is UserMessageException){
                    val message = getLocalizeMessage(e.resourceCode)
                    errorChannel.send(message)
                }
            }
        }
    }

    private fun getLocalizeMessage(resourceCode: ResourceCode) : String {
        return when(resourceCode){
            ResourceCode.JOINING_LIST_NOT_FOUND -> context.getString(R.string.error_list_not_found)
            ResourceCode.JOINING_USER_ALREADY_MEMBER -> context.getString(R.string.error_user_already_member)
            ResourceCode.UNKNOWN_ERROR -> context.getString(R.string.error_unknown)
            ResourceCode.NO_INTERNET_CONNECTION -> context.getString(R.string.error_no_internet_connection)
        }
    }
}