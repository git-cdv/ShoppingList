package chkan.ua.shoppinglist.core.services

import android.content.Context
import chkan.ua.core.exceptions.ResourceCode
import chkan.ua.core.exceptions.UserMessageException
import chkan.ua.domain.Logger
import chkan.ua.shoppinglist.BuildConfig
import chkan.ua.shoppinglist.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

interface ErrorHandler {
    suspend fun handle(e: Throwable, reason: String? = null)
    val errorChannelFlow: Flow<String>
}

class ErrorHandlerImpl @Inject constructor(
    private val logger: Logger,
    @ApplicationContext private val context: Context
) : ErrorHandler {

    private val errorChannel = Channel<String>()
    override val errorChannelFlow = errorChannel.receiveAsFlow()

    override suspend fun handle(e: Throwable, reason: String?) {
        logger.e(e, reason)
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

    private fun getLocalizeMessage(resourceCode: ResourceCode) : String {
        return when(resourceCode){
            ResourceCode.JOINING_LIST_NOT_FOUND -> context.getString(R.string.error_list_not_found)
            ResourceCode.JOINING_USER_ALREADY_MEMBER -> context.getString(R.string.error_user_already_member)
            ResourceCode.UNKNOWN_ERROR -> context.getString(R.string.error_unknown)
        }
    }
}