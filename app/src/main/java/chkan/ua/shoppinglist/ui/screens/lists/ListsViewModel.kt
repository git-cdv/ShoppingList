package chkan.ua.shoppinglist.ui.screens.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chkan.ua.core.services.ErrorHandler
import chkan.ua.domain.usecases.AddListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListsViewModel @Inject constructor(
    private val addList: AddListUseCase,
    private val errorHandler: ErrorHandler,
) : ViewModel() {

    fun addList(name: String){
        viewModelScope.launch (Dispatchers.IO) {
            try {
                addList.run(name)
            } catch (e: Exception){
                errorHandler.handle(e,addList)
            }
        }
    }
}