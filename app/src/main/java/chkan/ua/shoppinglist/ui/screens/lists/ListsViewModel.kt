package chkan.ua.shoppinglist.ui.screens.lists

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chkan.ua.domain.usecases.lists.AddListConfig
import chkan.ua.domain.usecases.lists.AddListUseCase
import chkan.ua.domain.usecases.lists.DeleteListUseCase
import chkan.ua.domain.usecases.lists.GetListsCountUseCase
import chkan.ua.domain.usecases.lists.GetListsFlowUseCase
import chkan.ua.domain.usecases.lists.MoveToTopUseCase
import chkan.ua.domain.usecases.lists.MoveTop
import chkan.ua.shoppinglist.core.services.ErrorHandler
import chkan.ua.shoppinglist.core.services.SharedPreferencesService
import chkan.ua.shoppinglist.core.services.SharedPreferencesServiceImpl.Companion.LAST_OPEN_LIST_ID_INT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListsViewModel @Inject constructor(
    getListsFlow: GetListsFlowUseCase,
    private val addList: AddListUseCase,
    private val getListsCount: GetListsCountUseCase,
    private val deleteList: DeleteListUseCase,
    private val moveToTop: MoveToTopUseCase,
    private val errorHandler: ErrorHandler,
    private val spService: SharedPreferencesService
) : ViewModel() {

    init {
        viewModelScope.launch (Dispatchers.IO) {
            val count = getListsCount.run(Unit)
            isListsExist = count > 0
            _isLoadReady.value = true
        }
    }

    val listsFlow = getListsFlow.run(Unit)
    private var isListsExist = false

    private val _isLoadReady = mutableStateOf(false)
    val isLoadReady: State<Boolean> = _isLoadReady

    fun addList(title: String){
        viewModelScope.launch (Dispatchers.IO) {
            try {
                val count = getListsCount.run(Unit)
                addList.run(AddListConfig(title,count + 1))
            } catch (e: Exception){
                errorHandler.handle(e,addList.getErrorReason())
            }
        }
    }

    fun deleteList(id: Int) {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                deleteList.run(id)
            } catch (e: Exception){
                errorHandler.handle(e,deleteList.getErrorReason())
            }
        }
    }

    fun isListExist() = isListsExist

    fun moveToTop(config: MoveTop) {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                moveToTop.run(config)
            } catch (e: Exception){
                errorHandler.handle(e,moveToTop.getErrorReason())
            }
        }
    }

    fun clearLastOpenListId() {
        spService.set(LAST_OPEN_LIST_ID_INT, 0)
    }
}