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
import chkan.ua.shoppinglist.core.services.ErrorHandler
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
    private val errorHandler: ErrorHandler,
) : ViewModel() {

    init {
        viewModelScope.launch (Dispatchers.IO) {
            val count = getListsCount.run(Unit)
            _isListsExist.value = ListsExistResult.Result(count > 0)
        }
    }

    val listsFlow = getListsFlow.run()

    private val _isListsExist = mutableStateOf<ListsExistResult>(ListsExistResult.Checking)
    val isListsExist: State<ListsExistResult> = _isListsExist

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
}

sealed class ListsExistResult{
    data object Checking : ListsExistResult()
    data class Result (val isExist: Boolean) : ListsExistResult()
}