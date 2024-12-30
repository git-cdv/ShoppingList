package chkan.ua.shoppinglist.ui.screens.items

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import chkan.ua.core.extensions.firstAsTitle
import chkan.ua.domain.models.Item
import chkan.ua.domain.objects.Editable
import chkan.ua.domain.usecases.history.AddItemInHistoryUseCase
import chkan.ua.domain.usecases.items.AddItemUseCase
import chkan.ua.domain.usecases.items.ClearReadyItemsUseCase
import chkan.ua.domain.usecases.items.DeleteItemUseCase
import chkan.ua.domain.usecases.items.EditItemUseCase
import chkan.ua.domain.usecases.items.GetItemsFlowUseCase
import chkan.ua.domain.usecases.items.MarkReadyConfig
import chkan.ua.domain.usecases.items.MarkReadyItemUseCase
import chkan.ua.domain.usecases.items.MoveItemToTopUseCase
import chkan.ua.domain.usecases.lists.MoveTop
import chkan.ua.shoppinglist.components.history_list.HistoryComponent
import chkan.ua.shoppinglist.core.components.ComponentsViewModel
import chkan.ua.shoppinglist.core.services.ErrorHandler
import chkan.ua.shoppinglist.core.services.SharedPreferencesService
import chkan.ua.shoppinglist.core.services.SharedPreferencesServiceImpl.Companion.LAST_OPEN_LIST_ID_INT
import chkan.ua.shoppinglist.core.services.SharedPreferencesServiceImpl.Companion.LAST_OPEN_LIST_TITLE_STR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class ItemsViewModel @Inject constructor(
    private val getItemsFlow: GetItemsFlowUseCase,
    private val addItem: AddItemUseCase,
    private val markReady: MarkReadyItemUseCase,
    private val deleteItem: DeleteItemUseCase,
    private val editItem: EditItemUseCase,
    private val clearReadyItems: ClearReadyItemsUseCase,
    private val addInHistory: AddItemInHistoryUseCase,
    private val errorHandler: ErrorHandler,
    private val historyComponent: HistoryComponent,
    private val spService: SharedPreferencesService,
    private val moveToTop: MoveItemToTopUseCase,
) : ComponentsViewModel() {

    init {
        attachComponent(historyComponent)
    }

    private val singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private val _isEmpty = mutableStateOf(false)
    val isEmpty: State<Boolean> = _isEmpty

    fun getFlowItemsByListId(listId: Int): Flow<List<Item>> {
        return getItemsFlow.run(listId).onEach { items ->
            _isEmpty.value = items.isEmpty()
        }
    }

    fun addItem(item: Item) {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                addItem.run(item)
            } catch (e: Exception){
                errorHandler.handle(e,addItem.getErrorReason(item))
            }
        }
        viewModelScope.launch (Dispatchers.IO) {
            try {
                addInHistory.run(item.content.firstAsTitle())
            } catch (e: Exception){
                errorHandler.handle(e,addInHistory.getErrorReason(item.content))
            }
        }
    }

    fun deleteItem(id: Int) {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                deleteItem.run(id)
            } catch (e: Exception){
                errorHandler.handle(e,deleteItem.getErrorReason())
            }
        }
    }

    fun changeReadyInItem(id: Int, state: Boolean) {
        viewModelScope.launch (singleThreadDispatcher) {
            val config = MarkReadyConfig(id,state)
            try {
                markReady.run(config)
            } catch (e: Exception){
                errorHandler.handle(e,markReady.getErrorReason(config))
            }
        }
    }

    fun clearReadyItems(listId: Int) {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                clearReadyItems.run(listId)
            } catch (e: Exception){
                errorHandler.handle(e,deleteItem.getErrorReason())
            }
        }
    }

    fun getHistoryComponent(listId: Int): HistoryComponent {
        return historyComponent.apply {
            initFlow(listId)
        }
    }

    fun saveLastOpenedList(listId: Int, listTitle: String) {
        spService.set(LAST_OPEN_LIST_ID_INT, listId)
        spService.set(LAST_OPEN_LIST_TITLE_STR, listTitle)
    }

    fun editItem(edited: Editable) {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                editItem.run(edited)
            } catch (e: Exception){
                errorHandler.handle(e,editItem.getErrorReason(edited))
            }
        }
    }

    fun moveToTop(config: MoveTop) {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                moveToTop.run(config)
            } catch (e: Exception){
                errorHandler.handle(e,moveToTop.getErrorReason())
            }
        }
    }

}