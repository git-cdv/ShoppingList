package chkan.ua.shoppinglist.ui.screens.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chkan.ua.domain.models.Item
import chkan.ua.domain.usecases.items.AddItemUseCase
import chkan.ua.domain.usecases.items.GetItemsFlowUseCase
import chkan.ua.shoppinglist.core.services.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemsViewModel @Inject constructor(
    private val getItemsFlow: GetItemsFlowUseCase,
    private val addItem: AddItemUseCase,
    private val errorHandler: ErrorHandler,
) : ViewModel() {

    fun getFlowItemsByListId(listId: Int): Flow<List<Item>> {
        return getItemsFlow.run(listId)
    }

    fun addItem(item: Item) {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                addItem.run(item)
            } catch (e: Exception){
                errorHandler.handle(e,addItem.getErrorReason())
            }
        }
    }

}