package chkan.ua.domain.usecases.share

import chkan.ua.core.interfaces.FlowUseCase
import chkan.ua.domain.Logger
import chkan.ua.domain.repos.RemoteRepository
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class GetRemoteItemsFlowUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val logger: Logger
) : FlowUseCase<String> {

    override fun invoke(config: String) =
        remoteRepository.getListWithItemsFlowById(config)
            .catch { throwable ->
                logger.e(throwable, getErrorReason(config))
                emit(emptyList())
            }


    override fun getErrorReason(config: String?) = "Failed to observe remote lists $config"
}