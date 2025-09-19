package chkan.ua.shoppinglist.ui.screens.lists

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chkan.ua.core.models.ListRole
import chkan.ua.core.models.isShared
import chkan.ua.domain.models.ListItemsUi
import chkan.ua.domain.models.ListProgress
import chkan.ua.domain.objects.Deletable
import chkan.ua.domain.objects.Editable
import chkan.ua.domain.usecases.lists.MoveTop
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.navigation.ItemsRoute
import chkan.ua.shoppinglist.navigation.localNavController
import chkan.ua.shoppinglist.session.SessionViewModel
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.AddListBottomSheet
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.ConfirmBottomSheet
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.EditBottomSheet
import chkan.ua.shoppinglist.ui.kit.items.ListItem
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(
    sessionViewModel: SessionViewModel,
    listsViewModel: ListsViewModel
) {
    val navController = localNavController.current
    val lists by listsViewModel.localListsFlow.collectAsStateWithLifecycle(initialValue = listOf())
    val sharedLists by listsViewModel.sharedListsFlow.collectAsStateWithLifecycle()

    //add list
    var showAddList by remember { mutableStateOf(false) }
    val addListState = rememberModalBottomSheetState()
    //stop sharing confirm
    var showConfirmStopSharing by remember { mutableStateOf(false) }
    val confirmStopSharingState = rememberModalBottomSheetState()
    var argStopSharingIdList by remember { mutableStateOf("") }
    //start sharing confirm
    var showConfirmStartSharing by remember { mutableStateOf(false) }
    val confirmStartSharingState = rememberModalBottomSheetState()
    var argStartSharingIdList by remember { mutableStateOf("") }
    //delete list
    var showConfirmDeleteList by remember { mutableStateOf(false) }
    val confirmDeleteListState = rememberModalBottomSheetState()
    var argDeletedIdList by remember { mutableStateOf(Deletable()) }
    //unfollow list
    var showConfirmUnfollowList by remember { mutableStateOf(false) }
    val confirmUnfollowListState = rememberModalBottomSheetState()
    var argUnfollowIdList by remember { mutableStateOf("") }
    //edit list
    var showEditBottomSheet by remember { mutableStateOf(false) }
    val editSheetState = rememberModalBottomSheetState()
    var editable by remember { mutableStateOf(Editable()) }
    //session
    val sessionState by sessionViewModel.sessionState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        sessionViewModel.clearLastOpenedList()
    }

    ListsScreenContent(
        lists,
        sharedLists,
        onListEvent = { event ->
            when (event) {
                is ListUiEvent.OnCardClick -> {
                    navController.navigate(
                        ItemsRoute(
                            event.list.id,
                            event.list.title,
                            event.list.role
                        )
                    )
                }

                ListUiEvent.OnCreateList -> {
                    if(sessionState.isSubscribed == true || lists.size < 2){
                        showAddList = true
                        scope.launch { addListState.show() }
                    } else {
                        sessionViewModel.showPaywall()
                    }
                }

                is ListUiEvent.OnDeleteList -> {
                    argDeletedIdList = Deletable(event.listId, event.isShared)
                    showConfirmDeleteList = true
                    scope.launch { confirmDeleteListState.show() }
                }

                is ListUiEvent.OnEditList -> {
                    editable = event.editable
                    showEditBottomSheet = true
                    scope.launch { editSheetState.show() }
                }

                is ListUiEvent.OnMoveToTop -> {
                    listsViewModel.moveToTop(MoveTop(event.listId, event.position))
                }

                is ListUiEvent.OnStopSharing -> {
                    argStopSharingIdList = event.listId
                    showConfirmStopSharing = true
                    scope.launch { confirmStopSharingState.show() }
                }

                is ListUiEvent.OnStopFollowing -> {
                    argUnfollowIdList = event.listId
                    showConfirmUnfollowList = true
                    scope.launch { confirmUnfollowListState.show() }
                }
                is ListUiEvent.OnShareList -> {
                    argStartSharingIdList = event.listId
                    showConfirmStartSharing = true
                    scope.launch { confirmStartSharingState.show() }
                }
            }
        }
    )

    if (showAddList) {
        AddListBottomSheet(
            addListState,
            onDismiss = { showAddList = false },
            addItem = { text ->
                listsViewModel.addList(text)
                scope.launch { addListState.hide() }.invokeOnCompletion {
                    showAddList = false
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

    if (showConfirmDeleteList) {
        ConfirmBottomSheet(
            confirmDeleteListState,
            question = stringResource(id = R.string.sure_delete_list),
            onConfirm = {
                scope.launch {
                    listsViewModel.onDeleteList(argDeletedIdList)
                    confirmDeleteListState.hide()
                    showConfirmDeleteList = false
                }
            },
            onDismiss = {
                scope.launch {
                    confirmDeleteListState.hide()
                    showConfirmDeleteList = false
                }
            }
        )
    }

    if (showConfirmUnfollowList) {
        ConfirmBottomSheet(
            confirmUnfollowListState,
            question = stringResource(id = R.string.sure_unfollow_list),
            onConfirm = {
                scope.launch {
                    listsViewModel.onUnfollow(argUnfollowIdList)
                    confirmUnfollowListState.hide()
                    showConfirmUnfollowList = false
                }
            },
            onDismiss = {
                scope.launch {
                    confirmUnfollowListState.hide()
                    showConfirmUnfollowList = false
                }
            }
        )
    }

    if (showConfirmStopSharing) {
        ConfirmBottomSheet(
            confirmStopSharingState,
            question = stringResource(id = R.string.sure_stop_sharing),
            onConfirm = {
                scope.launch {
                    listsViewModel.onStopSharing(argStopSharingIdList)
                    confirmStopSharingState.hide()
                    showConfirmStopSharing = false
                }
            },
            onDismiss = {
                scope.launch {
                    confirmStopSharingState.hide()
                    showConfirmStopSharing = false
                }
            }
        )
    }

    if (showConfirmStartSharing) {
        ConfirmBottomSheet(
            confirmStartSharingState,
            question = stringResource(id = R.string.sure_share_list),
            onConfirm = {
                if (sessionState.isSubscribed == true) {
                    scope.launch {
                        listsViewModel.createShareList(argStartSharingIdList)
                        confirmStartSharingState.hide()
                        showConfirmStartSharing = false
                    }
                } else {
                    sessionViewModel.showPaywall()
                }
            },
            onDismiss = {
                scope.launch {
                    confirmStartSharingState.hide()
                    showConfirmStartSharing = false
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
    var topbarTitleResId by remember { mutableIntStateOf(R.string.lists) }

    LaunchedEffect(lists.isEmpty(), sharedLists.isEmpty()) {
        topbarTitleResId = if (lists.isEmpty() && sharedLists.isNotEmpty()) {
            R.string.shared_lists
        } else {
            R.string.lists
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
                        text = stringResource(id = topbarTitleResId),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onListEvent(ListUiEvent.OnCreateList) },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.root_padding))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add List")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValue ->

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.root_padding)),
            contentPadding = PaddingValues(
                top = paddingValue.calculateTopPadding(),
                bottom = 136.dp
            ),
        ) {
            itemsIndexed(lists, key = { _, item -> item.id }) { index, list ->
                ListItem(
                    list = list,
                    modifier = Modifier.animateItem(),
                    onListEvent = onListEvent,
                    isFirst = index == 0
                )
            }
            if (sharedLists.isNotEmpty()) {
                if (lists.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.shared_lists),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                        )
                    }
                }
                items(sharedLists, key = { it.id }) { list ->
                    ListItem(
                        list = list,
                        modifier = Modifier.animateItem(),
                        onListEvent = onListEvent,
                        isFirst = false
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
                readyCount = 2, progress = ListProgress(count = 4, readyCount = 2), role = ListRole.LOCAL
            )
        )
        ListsScreenContent(
            list, list, { _ -> })
    }
}