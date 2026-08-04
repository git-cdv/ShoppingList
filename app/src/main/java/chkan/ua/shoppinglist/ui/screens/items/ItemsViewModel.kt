package chkan.ua.shoppinglist.ui.screens.items

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import chkan.ua.core.exceptions.ResourceCode
import chkan.ua.core.exceptions.UserMessageException
import chkan.ua.core.models.ListRole
import chkan.ua.core.models.isShared
import chkan.ua.core.models.toListRole
import chkan.ua.core.models.toPreferenceString
import chkan.ua.domain.Logger
import chkan.ua.domain.models.Item
import chkan.ua.domain.objects.Editable
import chkan.ua.domain.usecases.history.AddItemInHistoryUseCase
import chkan.ua.domain.usecases.items.AddItemUseCase
import chkan.ua.domain.usecases.items.ClearReadyConfig
import chkan.ua.domain.usecases.items.ClearReadyItemsUseCase
import chkan.ua.domain.usecases.items.DeleteItemUseCase
import chkan.ua.domain.usecases.items.EditItemUseCase
import chkan.ua.domain.usecases.items.GetItemsFlowUseCase
import chkan.ua.domain.usecases.items.ItemConfig
import chkan.ua.domain.usecases.items.MarkReadyConfig
import chkan.ua.domain.usecases.items.MarkReadyItemUseCase
import chkan.ua.domain.usecases.items.MoveItemToTopUseCase
import chkan.ua.domain.usecases.lists.MoveTop
import chkan.ua.domain.usecases.share.GetRemoteItemsFlowUseCase
import chkan.ua.domain.usecases.share.HasSharedListsUseCase
import chkan.ua.domain.usecases.share.ShareListUseCase
import chkan.ua.shoppinglist.components.history_list.HistoryComponent
import chkan.ua.shoppinglist.core.components.ComponentsViewModel
import chkan.ua.shoppinglist.core.services.ErrorHandler
import chkan.ua.shoppinglist.core.services.SharedPreferencesService
import chkan.ua.shoppinglist.core.services.SharedPreferencesServiceImpl.Companion.LAST_OPEN_LIST_ID_INT
import chkan.ua.shoppinglist.core.services.SharedPreferencesServiceImpl.Companion.LAST_OPEN_LIST_ROLE
import chkan.ua.shoppinglist.core.services.SharedPreferencesServiceImpl.Companion.LAST_OPEN_LIST_TITLE_STR
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.AddItemBottomSheetState
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.BottomSheetAction
import chkan.ua.shoppinglist.utils.AppEvent
import chkan.ua.shoppinglist.utils.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class ItemsViewModel @Inject constructor(
    private val getItemsFlow: GetItemsFlowUseCase,
    private val getRemoteItemsFlow: GetRemoteItemsFlowUseCase,
    private val addItem: AddItemUseCase,
    private val markReady: MarkReadyItemUseCase,
    private val deleteItem: DeleteItemUseCase,
    private val editItemUseCase: EditItemUseCase,
    private val clearReadyItems: ClearReadyItemsUseCase,
    private val addInHistory: AddItemInHistoryUseCase,
    private val errorHandler: ErrorHandler,
    private val historyComponent: HistoryComponent,
    private val spService: SharedPreferencesService,
    private val moveToTopUseCase: MoveItemToTopUseCase,
    private val shareList: ShareListUseCase,
    val eventBus: EventBus,
    private val logger: Logger,
    private val hasSharedListsUseCase: HasSharedListsUseCase,
    savedStateHandle: SavedStateHandle,
) : ComponentsViewModel() {

    init {
        attachComponent(historyComponent)
    }

    private val _state = MutableStateFlow(
        ItemsState(
            listId = savedStateHandle.get<String>("listId") ?: "",
            role = savedStateHandle.get<String>("role")?.toListRole() ?: ListRole.LOCAL
        )
    )
    val state: StateFlow<ItemsState> = _state.asStateFlow()

    private val _addItemBottomSheetState = mutableStateOf(AddItemBottomSheetState())
    val addItemBottomSheetState: State<AddItemBottomSheetState> = _addItemBottomSheetState

    private val singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private var itemsObservationJob: Job? = null


    fun observeItems(listId: String) {
        itemsObservationJob?.cancel()

        itemsObservationJob = getItemsFlow(listId)
            .onEach { items ->
                //TODO: check on optima
                //work in main thread (ok for not huge data)
                val (readyItems, notReadyItems) = items.partition { it.isReady }
                _state.update {
                    it.copy(
                        isEmpty = items.isEmpty(),
                        notReadyItems = notReadyItems,
                        readyItems = readyItems
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun observeRemoteItems(remoteId: String) {
        itemsObservationJob?.cancel()

        itemsObservationJob = getRemoteItemsFlow(remoteId)
            .onEach { items ->
                val (readyItems, notReadyItems) = items.partition { it.isReady }
                _state.update {
                    it.copy(
                        isEmpty = items.isEmpty(),
                        notReadyItems = notReadyItems,
                        readyItems = readyItems
                    )
                }
            }
            .launchIn(viewModelScope)
    }


    fun processIntent(intent: ItemsIntent) {
        logger.d("ITEMS_VM","processIntent: $intent")
        when (intent) {
            is ItemsIntent.AddItem -> addItem(intent.title, intent.note)
            is ItemsIntent.ClearReadyItems -> clearReadyItems(intent.listId)
            is ItemsIntent.DeleteItem -> deleteItem(intent.item)
            is ItemsIntent.EditItem -> editItem(intent.editable)
            is ItemsIntent.MarkReady -> changeReadyInItem(intent.item, intent.state)
            is ItemsIntent.MoveToTop -> moveToTop(MoveTop(intent.id, intent.position))
            is ItemsIntent.ShareList -> createShareList(intent.listId)
        }
    }

    fun processAddItemBottomSheetChange(action: BottomSheetAction) {
        _addItemBottomSheetState.value = when (action) {
            is BottomSheetAction.SetIsOpen -> _addItemBottomSheetState.value.copy(isOpen = action.isOpen)
            is BottomSheetAction.SetText -> _addItemBottomSheetState.value.copy(text = action.text)
        }
    }

    fun createShareList(listId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            withLoading {
                shareList(listId)
                    .onSuccess { remoteId ->
                        observeRemoteItems(remoteId)
                        _state.update { it.copy(role = ListRole.SHARED_OWNER, listId = remoteId) }
                        eventBus.sendEvent(AppEvent.SharedSuccess(remoteId))
                        hasSharedListsUseCase.setState(true)
                    }
                    .onFailure {
                        errorHandler.handle(UserMessageException(ResourceCode.SHARING_ERROR_CREATE_SHARED_LIST))
                    }
            }
        }
    }

    private fun addItem(title: String, note: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val config = ItemConfig(
                Item(
                    itemId = UUID.randomUUID().toString().take(6),
                    content = title,
                    listId = _state.value.listId,
                    note = note,
                ), _state.value.role.isShared
            )
            try {
                addItem(config)
                delay(2000)
                addInHistory(title)
            } catch (e: Exception) {
                errorHandler.handle(e, addItem.getErrorReason(config))
            }
        }
    }

    private fun deleteItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            val isShared = _state.value.role.isShared
            val config = ItemConfig(item, isShared)
            try {
                if (isShared) {
                    withLoading { deleteItem(config) }
                } else {
                    deleteItem(config)
                }
            } catch (e: Exception) {
                errorHandler.handle(e, deleteItem.getErrorReason())
            }
        }
    }

    private fun changeReadyInItem(item: Item, state: Boolean) {
        viewModelScope.launch(singleThreadDispatcher) {
            val isShared = _state.value.role.isShared
            val config =
                MarkReadyConfig(item.itemId, listId = item.listId, state, isShared)
            try {
                if (isShared) {
                    withLoading { markReady(config) }
                } else {
                    markReady(config)
                }
            } catch (e: Exception) {
                errorHandler.handle(e, markReady.getErrorReason(config))
            }
        }
    }

    private fun clearReadyItems(listId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val isShared = _state.value.role.isShared
                if (isShared) {
                    withLoading {
                        clearReadyItems(ClearReadyConfig(listId, true))
                    }
                } else {
                    clearReadyItems(ClearReadyConfig(listId, false))
                }
            } catch (e: Exception) {
                errorHandler.handle(e, deleteItem.getErrorReason())
            }
        }
    }

    fun getHistoryComponent(listId: String): HistoryComponent {
        return historyComponent.apply {
            initFlow(listId)
        }
    }

    fun saveLastOpenedList(listId: String, listTitle: String, role: ListRole) {
        spService.set(LAST_OPEN_LIST_ID_INT, listId)
        spService.set(LAST_OPEN_LIST_TITLE_STR, listTitle)
        spService.set(LAST_OPEN_LIST_ROLE, role.toPreferenceString())
    }

    private fun editItem(edited: Editable) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val isShared = _state.value.role.isShared
                if (isShared) {
                    withLoading { editItemUseCase(edited.copy(listId = _state.value.listId, isShared = true)) }
                } else {
                    editItemUseCase(edited.copy(listId = _state.value.listId, isShared = false))
                }
            } catch (e: Exception) {
                errorHandler.handle(e, editItemUseCase.getErrorReason(edited))
            }
        }
    }

    private fun moveToTop(config: MoveTop) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                moveToTopUseCase(config)
            } catch (e: Exception) {
                errorHandler.handle(e, moveToTopUseCase.getErrorReason())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        itemsObservationJob?.cancel()
    }

}