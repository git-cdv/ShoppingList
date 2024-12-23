package chkan.ua.shoppinglist.ui.screens.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import chkan.ua.domain.models.Item
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.navigation.ItemsRoute
import chkan.ua.shoppinglist.navigation.localNavController
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.AddItemBottomSheet
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.ConfirmBottomSheet
import chkan.ua.shoppinglist.ui.kit.empty_state.CenteredTextScreen
import chkan.ua.shoppinglist.ui.kit.items.ItemItem
import chkan.ua.shoppinglist.ui.kit.items.ReadyItem
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
    val isEmptyState by itemsViewModel.isEmpty
    val items by itemsViewModel.getFlowItemsByListId(listId).collectAsStateWithLifecycle(initialValue = listOf())
    val (readyItems, notReadyItems) = items.partition { it.isReady }
    val historyComponent = itemsViewModel.getHistoryComponent()

    var showAddItemBottomSheet by remember { mutableStateOf(false) }
    val addItemSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        itemsViewModel.saveLastOpenListId(listId)
    }

    ItemsScreenContent(
        title = listTitle,
        items = notReadyItems,
        readyItems = readyItems,
        isEmptyState = isEmptyState,
        handleAddItemSheet = { isShow ->
            showAddItemBottomSheet = isShow
            if (isShow) {
                scope.launch { addItemSheetState.show() }
            } else {
                scope.launch { addItemSheetState.hide() }
            }
         },
        onDeleteItem = { id -> itemsViewModel.deleteItem(id) },
        onMarkReady = { id, state -> itemsViewModel.changeReadyInItem(id, state) },
        goToBack = {navController.popBackStack()},
        clearReadyItems = {itemsViewModel.clearReadyItems(listId)}
    )

    if (showAddItemBottomSheet){
        AddItemBottomSheet(
            addItemSheetState,
            historyComponent,
            onDismiss = { showAddItemBottomSheet = false },
            addItem = { title -> itemsViewModel.addItem(Item(content = title, listId = listId))},
            R.string.items_text_placeholder
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
) {
    var showConfirmBottomSheet by remember { mutableStateOf(false) }
    val confirmSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = title, color = Color.Gray) },
                navigationIcon = {
                    IconButton(onClick = { goToBack.invoke() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            tint = Color.Gray,
                            contentDescription = "Back")
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
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.root_padding))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValue ->

        LaunchedEffect(isEmptyState) {
            if (isEmptyState){
                handleAddItemSheet.invoke(true)
            }
        }

        if (isEmptyState){
            CenteredTextScreen(stringResource(id = R.string.text_empty_items))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 64.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValue.calculateTopPadding())
                    .background(MaterialTheme.colorScheme.background)
            ) {
                items(items, key = { it.itemId }) { item ->
                    ItemItem(
                        text = item.content,
                        modifier = Modifier.animateItem(),
                        onReady = { onMarkReady.invoke(item.itemId, true) },
                        onDeleteList = { onDeleteItem.invoke(item.itemId) })
                }
                if (readyItems.isNotEmpty()) {
                    item {
                        HorizontalDivider(
                            color = Color.LightGray,
                            thickness = 1.dp,
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.root_padding))
                        )
                    }
                    if (readyItems.size > 3) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = dimensionResource(id = R.dimen.root_padding))
                            ) {
                                Text(
                                    text = stringResource(id = R.string.clear),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Gray,
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)))
                                        .clickable {
                                            showConfirmBottomSheet = true
                                            scope.launch { confirmSheetState.show() }
                                        }
                                        .padding(dimensionResource(id = R.dimen.inner_padding))

                                )
                            }
                        }
                    }
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

        if (showConfirmBottomSheet){
            ConfirmBottomSheet(
                confirmSheetState,
                question = stringResource(id = R.string.sure_clear_everything),
                onConfirm = { scope.launch {
                    clearReadyItems.invoke()
                    confirmSheetState.hide()
                    showConfirmBottomSheet = false
                    } },
                onDismiss = { scope.launch {
                    confirmSheetState.hide()
                    showConfirmBottomSheet = false
                } }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemsScreenContentPreview() {
    ShoppingListTheme {
        ItemsScreenContent("Title", listOf(
            Item(333,"Item 777", 0,0, false),
            Item(444,"Item 2", 0,1, false)
        ), listOf(
            Item(55,"Item 1", 0,0, false),
            Item(44774,"Item 2", 0,1, false)
        ), false, {},{_,_ -> }, {}, {},{})
    }
}