package chkan.ua.shoppinglist.ui.screens.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chkan.ua.core.extensions.firstAsTitle
import chkan.ua.domain.models.Item
import chkan.ua.domain.objects.Editable
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.navigation.ItemsRoute
import chkan.ua.shoppinglist.navigation.localNavController
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.AddItemBottomSheet
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.BottomSheetAction
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.ConfirmBottomSheet
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.EditBottomSheet
import chkan.ua.shoppinglist.ui.kit.empty_state.CenteredTextScreen
import chkan.ua.shoppinglist.ui.kit.items.ItemItem
import chkan.ua.shoppinglist.ui.kit.items.ReadyItem
import chkan.ua.shoppinglist.ui.kit.togglers.ToggleShowCompleted
import chkan.ua.shoppinglist.ui.kit.togglers.ToggleShowText
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    args: ItemsRoute,
    itemsViewModel: ItemsViewModel = hiltViewModel()
) {
    val navController = localNavController.current
    val listId = args.listId
    val listTitle = args.listTitle

    LaunchedEffect(Unit) {
        itemsViewModel.observeItems(listId)
        itemsViewModel.saveLastOpenedList(listId, listTitle)
    }

    val uiState by itemsViewModel.state.collectAsStateWithLifecycle()

    val addItemBottomSheetState by itemsViewModel.addItemBottomSheetState
    val addItemSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showEditBottomSheet by remember { mutableStateOf(false) }
    val editSheetState = rememberModalBottomSheetState()
    var editable by remember { mutableStateOf(Editable()) }
    val scope = rememberCoroutineScope()

    ItemsScreenContent(
        title = listTitle,
        items = uiState.notReadyItems,
        readyItems = uiState.readyItems,
        isEmptyState = uiState.isEmpty,
        handleAddItemSheet = { isShow ->
            itemsViewModel.processAddItemBottomSheetChange(BottomSheetAction.SetIsOpen(isShow))
            if (isShow) {
                scope.launch { addItemSheetState.show() }
            } else {
                scope.launch { addItemSheetState.hide() }
            }
        },
        onDeleteItem = { id -> itemsViewModel.processIntent(ItemsIntent.DeleteItem(id)) },
        onMarkReady = { id, state ->
            itemsViewModel.processIntent(
                ItemsIntent.MarkReady(
                    id,
                    state
                )
            )
        },
        goToBack = { navController.popBackStack() },
        clearReadyItems = { itemsViewModel.processIntent(ItemsIntent.ClearReadyItems(listId)) },
        onEditItem = { edited ->
            editable = edited
            showEditBottomSheet = true
            scope.launch { editSheetState.show() }
        },
        onMoveToTop = { id, position ->
            itemsViewModel.processIntent(
                ItemsIntent.MoveToTop(
                    id,
                    position
                )
            )
        }
    )

    if (addItemBottomSheetState.isOpen) {
        AddItemBottomSheet(
            addItemSheetState,
            listId,
            onDismiss = {
                itemsViewModel.processAddItemBottomSheetChange(
                    BottomSheetAction.SetIsOpen(
                        false
                    )
                )
            },
            addItem = { addedItem ->
                itemsViewModel.processIntent(
                    ItemsIntent.AddItem(
                        Item(
                            content = addedItem.content.firstAsTitle(),
                            listId = listId,
                            note = addedItem.note
                        )
                    )
                )
            },
            R.string.items_text_placeholder
        )
    }

    if (showEditBottomSheet) {
        EditBottomSheet(
            editSheetState,
            onDismiss = { showEditBottomSheet = false },
            onEdit = { edited -> itemsViewModel.processIntent(ItemsIntent.EditItem(edited)) },
            editable = editable
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreenContent(
    title: String,
    items: List<Item>,
    readyItems: List<Item>,
    isEmptyState: Boolean,
    handleAddItemSheet: (Boolean) -> Unit,
    onMarkReady: (Int, Boolean) -> Unit,
    onDeleteItem: (Int) -> Unit,
    goToBack: () -> Unit,
    clearReadyItems: () -> Unit,
    onEditItem: (Editable) -> Unit,
    onMoveToTop: (Int, Int) -> Unit,
) {
    var showConfirmBottomSheet by remember { mutableStateOf(false) }
    val confirmSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var isReadyShown by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { goToBack.invoke() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    handleAddItemSheet.invoke(true)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.root_padding))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValue ->

        LaunchedEffect(isEmptyState) {
            if (isEmptyState) {
                handleAddItemSheet.invoke(true)
            }
        }

        if (isEmptyState) {
            CenteredTextScreen(stringResource(id = R.string.text_empty_items))
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 4.dp,bottom = 144.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValue.calculateTopPadding(),
                        start = dimensionResource(R.dimen.root_padding),
                        end = dimensionResource(R.dimen.root_padding)
                    )
            ) {
                itemsIndexed(items, key = { _, item -> item.itemId }) { index, item ->
                    ItemItem(
                        text = item.content,
                        note = item.note,
                        modifier = Modifier.animateItem(),
                        onReady = { onMarkReady.invoke(item.itemId, true) },
                        onDelete = { onDeleteItem.invoke(item.itemId) },
                        onEdit = { onEditItem.invoke(Editable(item.itemId, item.content, note = item.note)) },
                        onMoveToTop = { onMoveToTop.invoke(item.itemId, item.position) },
                        isFirst = index == 0
                    )
                }

                if (readyItems.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        ToggleShowCompleted(
                            isShowing = isReadyShown,
                            showText = stringResource(id = R.string.show_completed) + " (${readyItems.size})",
                            hideText = stringResource(id = R.string.hide_completed) + " (${readyItems.size})",
                            onToggle = {
                                isReadyShown = it
                            },
                            onClearAll = {
                                showConfirmBottomSheet = true
                                scope.launch { confirmSheetState.show() }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (isReadyShown) {
                    items(readyItems, key = { it.itemId }) { item ->
                        ReadyItem(
                            text = item.content,
                            modifier = Modifier.animateItem(),
                            onNotReady = { onMarkReady.invoke(item.itemId, false) },
                            onDeleteItem = { onDeleteItem.invoke(item.itemId) })
                    }
                }
            }
        }

        if (showConfirmBottomSheet) {
            ConfirmBottomSheet(
                confirmSheetState,
                question = stringResource(id = R.string.sure_clear_everything),
                onConfirm = {
                    scope.launch {
                        clearReadyItems.invoke()
                        confirmSheetState.hide()
                        showConfirmBottomSheet = false
                    }
                },
                onDismiss = {
                    scope.launch {
                        confirmSheetState.hide()
                        showConfirmBottomSheet = false
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemsScreenContentPreview() {
    ShoppingListTheme {
        ItemsScreenContent(
            "Title", listOf(
            Item(333, "Item 777", 0, 0, false),
            Item(444, "Item 2", 0, 1, false)
        ), listOf(
            Item(55, "Item 1", 0, 0, false),
            Item(44774, "Item 2", 0, 1, false)
        ), false, {}, { _, _ -> }, {}, {}, {}, {}, { _, _ -> })
    }
}