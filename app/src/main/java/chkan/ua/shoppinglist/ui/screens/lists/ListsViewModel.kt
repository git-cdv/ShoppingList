package chkan.ua.shoppinglist.ui.screens.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chkan.ua.core.exceptions.ResourceCode
import chkan.ua.core.exceptions.UserMessageException
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
import chkan.ua.domain.usecases.share.HasSharedListsUseCase
import chkan.ua.domain.usecases.share.JoinListUseCase
import chkan.ua.domain.usecases.share.ShareListUseCase
import chkan.ua.domain.usecases.share.StopSharingUseCase
import chkan.ua.domain.usecases.share.UnfollowUseCase
import chkan.ua.shoppinglist.core.services.ErrorHandler
import chkan.ua.shoppinglist.utils.AppEvent
import chkan.ua.shoppinglist.utils.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
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
    private val hasSharedListsUseCase: HasSharedListsUseCase,
    private val unfollow: UnfollowUseCase,
    val eventBus: EventBus,
) : ViewModel() {

    init {
        observeHasSharedLists()
    }

    val localListsFlow = getListsFlow(Unit)

    private val _sharedListsFlow = MutableStateFlow<List<ListItemsUi>>(emptyList())
    val sharedListsFlow: StateFlow<List<ListItemsUi>> = _sharedListsFlow.asStateFlow()

    private var sharedObservationJob: Job? = null

    private fun observeHasSharedLists() {
        viewModelScope.launch {
            hasSharedListsUseCase.observe().distinctUntilChanged().collect { isHasSharedLists ->
                logger.d("LISTS_VM","isHasSharedLists: $isHasSharedLists")
                if (isHasSharedLists == true) {
                    observeSharedLists()
                }
            }
        }
    }

    private fun observeSharedLists() {
        if (sharedObservationJob?.isActive == true) return

        sharedObservationJob = viewModelScope.launch (Dispatchers.IO) {
            getSharedListsFlow(Unit).collect { lists ->
                logger.d("LISTS_VM","shared lists size: ${lists.size}")
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
                errorHandler.handle(UserMessageException(ResourceCode.SHARING_ERROR_STOP_SHARING_LIST))
            }
        }
    }

    fun createShareList(listId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            shareList(listId)
                .onSuccess {
                    hasSharedListsUseCase.setState(true)
                }
                .onFailure {
                    errorHandler.handle(UserMessageException(ResourceCode.SHARING_ERROR_CREATE_SHARED_LIST))
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        sharedObservationJob?.cancel()
    }

    fun onUnfollow(listId: String) {
        viewModelScope.launch{
            unfollow(listId)
                .onSuccess {
                    eventBus.sendEvent(AppEvent.GoToBackAfterUnfollow)
                }
                .onFailure {
                    errorHandler.handle(it, it.message)
                }
        }
    }
}