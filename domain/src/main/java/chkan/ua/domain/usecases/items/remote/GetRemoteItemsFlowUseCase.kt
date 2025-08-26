package chkan.ua.domain.usecases.items.remote

import chkan.ua.core.interfaces.FlowUseCase
import chkan.ua.domain.repos.RemoteRepository
import javax.inject.Inject

class GetRemoteItemsFlowUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) : FlowUseCase<String> {

    override fun invoke(config: String) = remoteRepository.getListWithItemsFlowById(config)

    override fun getErrorReason(config: String?) = "Failed to observe lists $config"
}