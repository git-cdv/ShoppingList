package chkan.ua.shoppinglist.ui.screens.lists

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chkan.ua.domain.models.ListItemsUi
import chkan.ua.domain.models.ListProgress
import chkan.ua.domain.objects.Deletable
import chkan.ua.domain.objects.Editable
import chkan.ua.domain.usecases.lists.MoveTop
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.navigation.ItemsRoute
import chkan.ua.shoppinglist.navigation.localNavController
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.AddListBottomSheet
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.ConfirmBottomSheet
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.EditBottomSheet
import chkan.ua.shoppinglist.ui.kit.items.ListItem
import chkan.ua.shoppinglist.ui.kit.items.ListRole
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(
    listsViewModel: ListsViewModel = hiltViewModel()
) {
    val navController = localNavController.current
    val lists by listsViewModel.localListsFlow.collectAsStateWithLifecycle(initialValue = listOf())
    val sharedLists by listsViewModel.sharedListsFlow.collectAsStateWithLifecycle()
    var argDeletedIdList by remember { mutableStateOf(Deletable()) }
    var editable by remember { mutableStateOf(Editable()) }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showConfirmBottomSheet by remember { mutableStateOf(false) }
    val confirmSheetState = rememberModalBottomSheetState()
    var showEditBottomSheet by remember { mutableStateOf(false) }
    val editSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        listsViewModel.clearLastOpenedList()
        listsViewModel.observeSharedLists()
    }

    ListsScreenContent(
        lists,
        sharedLists,
        onListEvent = { event ->
            when(event){
                is ListUiEvent.OnCardClick -> {
                    navController.navigate(
                        ItemsRoute(
                            event.list.id,
                            event.list.title,
                            event.list.isShared
                        )
                    )
                }
                ListUiEvent.OnCreateList -> {
                    showBottomSheet = true
                    scope.launch { sheetState.show() }
                }
                is ListUiEvent.OnDeleteList -> {
                    argDeletedIdList = Deletable(event.listId, event.isShared)
                    showConfirmBottomSheet = true
                    scope.launch { confirmSheetState.show() }
                }
                is ListUiEvent.OnEditList -> {
                    editable = event.editable
                    showEditBottomSheet = true
                    scope.launch { editSheetState.show() }
                }
                is ListUiEvent.OnMoveToTop -> {
                    listsViewModel.moveToTop(MoveTop(event.listId, event.position))
                }
                is ListUiEvent.OnStopSharing -> {}
                is ListUiEvent.OnStopFollowing -> {}
            }
        }
    )

    if (showBottomSheet) {
        AddListBottomSheet(
            sheetState,
            onDismiss = { showBottomSheet = false },
            addItem = { text ->
                listsViewModel.addList(text)
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    showBottomSheet = false
                }
            },
            R.string.first_list_text_placeholder
        )
    }

    if (showEditBottomSheet) {
        EditBottomSheet(
            editSheetState,
            onDismiss = { showEditBottomSheet = false },
            onEdit = { edited -> listsViewModel.onEditList(edited) },
            editable = editable,
            isList = true
        )
    }

    if (showConfirmBottomSheet) {
        ConfirmBottomSheet(
            confirmSheetState,
            question = stringResource(id = R.string.sure_delete_list),
            onConfirm = {
                scope.launch {
                    listsViewModel.onDeleteList(argDeletedIdList)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreenContent(
    lists: List<ListItemsUi>,
    sharedLists: List<ListItemsUi>,
    onListEvent: (ListUiEvent) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()
    var fabVisible by remember { mutableStateOf(true) }

    LaunchedEffect(listState) {
        var lastScroll = 0
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .collect { offset ->
                if (offset > lastScroll) {
                    fabVisible = false // скроллим вниз → скрыть FAB
                } else if (offset < lastScroll) {
                    fabVisible = true // скроллим вверх → показать FAB
                }
                lastScroll = offset
            }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.lists),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabVisible,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                FloatingActionButton(
                    onClick = { onListEvent(ListUiEvent.OnCreateList) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.root_padding))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add List")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValue ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.root_padding)),
            contentPadding = PaddingValues(
                top = paddingValue.calculateTopPadding(),
                bottom = paddingValue.calculateBottomPadding() + 8.dp
            ),
        ) {
            itemsIndexed(lists, key = { _, item -> item.id }) { index, list ->
                ListItem(
                    list = list,
                    modifier = Modifier.animateItem(),
                    onListEvent = onListEvent,
                    isFirst = index == 0,
                    role = ListRole.LOCAL
                )
            }
            if (sharedLists.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.shared_lists),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                items(sharedLists, key = { it.id }) { list ->
                    ListItem(
                        list = list,
                        modifier = Modifier.animateItem(),
                        onListEvent = onListEvent,
                        isFirst = false,
                        role = if (list.isOwner) ListRole.SHARED_OWNER else ListRole.SHARED_MEMBER
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListsScreenContentPreview() {
    ShoppingListTheme {
        val list = listOf(
            ListItemsUi(
                id = "6187",
                title = "Commodo",
                position = 1,
                count = 4,
                readyCount = 2, progress = ListProgress(count = 4, readyCount = 2), isShared = false
            )
        )
        ListsScreenContent(
            list, list, {_->})
    }
}