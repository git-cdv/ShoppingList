package chkan.ua.shoppinglist.ui.screens.lists

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chkan.ua.domain.Logger
import chkan.ua.domain.models.ListItemsUi
import chkan.ua.domain.objects.Deletable
import chkan.ua.domain.objects.Editable
import chkan.ua.domain.objects.LastOpenedList
import chkan.ua.domain.usecases.lists.AddListUseCase
import chkan.ua.domain.usecases.lists.DeleteListUseCase
import chkan.ua.domain.usecases.lists.EditListUseCase
import chkan.ua.domain.usecases.lists.GetListsCountUseCase
import chkan.ua.domain.usecases.lists.GetListsFlowUseCase
import chkan.ua.domain.usecases.lists.MoveToTopUseCase
import chkan.ua.domain.usecases.lists.MoveTop
import chkan.ua.domain.usecases.share.GetSharedListsFlowUseCase
import chkan.ua.domain.usecases.share.ShareListUseCase
import chkan.ua.domain.usecases.share.StopSharingUseCase
import chkan.ua.shoppinglist.core.services.ErrorHandler
import chkan.ua.shoppinglist.core.services.SharedPreferencesService
import chkan.ua.shoppinglist.core.services.SharedPreferencesServiceImpl.Companion.LAST_OPEN_LIST_ID_INT
import chkan.ua.shoppinglist.core.services.SharedPreferencesServiceImpl.Companion.LAST_OPEN_LIST_IS_SHARED
import chkan.ua.shoppinglist.core.services.SharedPreferencesServiceImpl.Companion.LAST_OPEN_LIST_TITLE_STR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListsViewModel @Inject constructor(
    getListsFlow: GetListsFlowUseCase,
    private val getSharedListsFlow: GetSharedListsFlowUseCase,
    private val addList: AddListUseCase,
    private val getListsCount: GetListsCountUseCase,
    private val deleteList: DeleteListUseCase,
    private val editList: EditListUseCase,
    private val moveToTop: MoveToTopUseCase,
    val errorHandler: ErrorHandler,
    private val spService: SharedPreferencesService,
    private val stopSharing: StopSharingUseCase,
    private val logger: Logger,
    private val shareList: ShareListUseCase,
) : ViewModel() {

    init {
        viewModelScope.launch (Dispatchers.IO) {
            val splashMinShowTime = 200L

            val dataJob = launch {
                val count = getListsCount.invoke(Unit)
                isListsExist = count > 0
            }

            delay(splashMinShowTime)
            dataJob.join()
            _isLoadReady.value = true
        }
    }

    val localListsFlow = getListsFlow(Unit)

    private val _sharedListsFlow = MutableStateFlow<List<ListItemsUi>>(emptyList())
    val sharedListsFlow: StateFlow<List<ListItemsUi>> = _sharedListsFlow.asStateFlow()

    private var sharedObservationJob: Job? = null
    private var isListsExist = false

    private val _isLoadReady = mutableStateOf(false)
    val isLoadReady: State<Boolean> = _isLoadReady

    fun observeSharedLists() {
        if (sharedObservationJob?.isActive == true) return

        sharedObservationJob = viewModelScope.launch (Dispatchers.IO) {
            getSharedListsFlow(Unit).collect {lists ->
                _sharedListsFlow.value = lists
                isListsExist = lists.isNotEmpty()
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

    fun isListExist() = isListsExist

    fun moveToTop(config: MoveTop) {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                moveToTop.invoke(config)
            } catch (e: Exception){
                errorHandler.handle(e,moveToTop.getErrorReason())
            }
        }
    }

    fun clearLastOpenedList() {
        spService.set(LAST_OPEN_LIST_ID_INT, 0)
        spService.set(LAST_OPEN_LIST_TITLE_STR, "")
        spService.set(LAST_OPEN_LIST_IS_SHARED, false)
    }

    fun getLastOpenedList(): LastOpenedList? {
        return try {
            val id = spService.get(LAST_OPEN_LIST_ID_INT, String::class.java) ?: ""
            val title = spService.get(LAST_OPEN_LIST_TITLE_STR, String::class.java) ?: ""
            val isShared = spService.get(LAST_OPEN_LIST_IS_SHARED, Boolean::class.java) ?: false
            LastOpenedList(id,title,isShared)
        } catch (e: Exception) {
            null
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
                    errorHandler.handle(Exception(it),"Error while sharing list. Please try again later.")
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        sharedObservationJob?.cancel()
    }
}