package chkan.ua.shoppinglist.ui.screens.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chkan.ua.domain.Logger
import chkan.ua.domain.models.ListItemsUi
import chkan.ua.domain.objects.Deletable
import chkan.ua.domain.objects.Editable
import chkan.ua.domain.usecases.lists.AddListUseCase
import chkan.ua.domain.usecases.lists.DeleteListUseCase
import chkan.ua.domain.usecases.lists.EditListUseCase
import chkan.ua.domain.usecases.lists.GetListsFlowUseCase
import chkan.ua.domain.usecases.lists.MoveToTopUseCase
import chkan.ua.domain.usecases.lists.MoveTop
import chkan.ua.domain.usecases.share.GetSharedListsFlowUseCase
import chkan.ua.domain.usecases.share.JoinListUseCase
import chkan.ua.domain.usecases.share.ShareListUseCase
import chkan.ua.domain.usecases.share.StopSharingUseCase
import chkan.ua.shoppinglist.core.services.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListsViewModel @Inject constructor(
    getListsFlow: GetListsFlowUseCase,
    private val getSharedListsFlow: GetSharedListsFlowUseCase,
    private val addList: AddListUseCase,
    private val deleteList: DeleteListUseCase,
    private val editList: EditListUseCase,
    private val moveToTop: MoveToTopUseCase,
    val errorHandler: ErrorHandler,
    private val stopSharing: StopSharingUseCase,
    private val logger: Logger,
    private val shareList: ShareListUseCase,
    private val joinList: JoinListUseCase,
) : ViewModel() {

    val localListsFlow = getListsFlow(Unit)

    private val _sharedListsFlow = MutableStateFlow<List<ListItemsUi>>(emptyList())
    val sharedListsFlow: StateFlow<List<ListItemsUi>> = _sharedListsFlow.asStateFlow()

    private var sharedObservationJob: Job? = null

    fun observeSharedLists() {
        if (sharedObservationJob?.isActive == true) return

        sharedObservationJob = viewModelScope.launch (Dispatchers.IO) {
            getSharedListsFlow(Unit).collect {lists ->
                _sharedListsFlow.value = lists
            }
        }
    }
    fun addList(title: String){
        viewModelScope.launch (Dispatchers.IO) {
            try {
                addList.invoke(title)
            } catch (e: Exception){
                errorHandler.handle(e,addList.getErrorReason())
            }
        }
    }
    fun onDeleteList(deletable: Deletable) {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                deleteList(deletable)
            } catch (e: Exception){
                errorHandler.handle(e,deleteList.getErrorReason())
            }
        }
    }

    fun moveToTop(config: MoveTop) {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                moveToTop.invoke(config)
            } catch (e: Exception){
                errorHandler.handle(e,moveToTop.getErrorReason())
            }
        }
    }

    fun onEditList(editable: Editable) {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                editList(editable)
            } catch (e: Exception){
                errorHandler.handle(e,editList.getErrorReason(editable))
            }
        }
    }

    fun onStopSharing(listId: String) {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                stopSharing(listId)
            } catch (e: Exception){
                logger.e(e)
                errorHandler.handle(e,"Error while stopping sharing list. Please try again later.")
            }
        }
    }

    fun createShareList(listId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            shareList(listId)
                .onFailure {
                    errorHandler.handle(it,"Error while sharing list. Please try again later.")
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        sharedObservationJob?.cancel()
    }

    fun onJoinList(inviteCode: String?) {
        inviteCode?.let { code ->
            viewModelScope.launch{
                joinList(code)
                    .onFailure {
                        errorHandler.handle(it, it.message)
                    }
            }
        }
    }
}