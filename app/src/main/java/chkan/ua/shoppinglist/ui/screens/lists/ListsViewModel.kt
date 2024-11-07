package chkan.ua.shoppinglist.ui.screens.lists

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chkan.ua.shoppinglist.core.services.ErrorHandler
import chkan.ua.domain.usecases.AddListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListsViewModel @Inject constructor(
    private val addList: AddListUseCase,
    private val errorHandler: ErrorHandler,
) : ViewModel() {

    init {
        getLists()
    }

    private val _isListsExist = mutableStateOf<ListsExistResult>(ListsExistResult.Checking)
    val isListsExist: State<ListsExistResult> = _isListsExist

    private fun getLists() {
        viewModelScope.launch (Dispatchers.IO) {
            delay(2000)
            _isListsExist.value = ListsExistResult.Result(false)
        }
    }

    fun addList(name: String){
        viewModelScope.launch (Dispatchers.IO) {
            try {
                addList.run(name)
            } catch (e: Exception){
                errorHandler.handle(e,addList.getErrorReason())
            }
        }
    }
}

sealed class ListsExistResult{
    data object Checking : ListsExistResult()
    data class Result (val isExist: Boolean) : ListsExistResult()
}