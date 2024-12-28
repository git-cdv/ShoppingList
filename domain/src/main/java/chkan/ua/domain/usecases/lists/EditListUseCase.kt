package chkan.ua.domain.usecases.lists

import chkan.ua.core.interfaces.SuspendUseCase
import chkan.ua.domain.objects.Editable
import chkan.ua.domain.repos.ListsRepository
import javax.inject.Inject

class EditListUseCase @Inject constructor(
    private val listsRepository: ListsRepository
) : SuspendUseCase<Editable> {

    override suspend fun run(config: Editable) {
        listsRepository.updateTitle(config)
    }

    override fun getErrorReason(config: Editable?) = "Failed to edit list $config"
}