package chkan.ua.shoppinglist.ui.screens.items

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chkan.ua.core.extensions.firstAsTitle
import chkan.ua.core.models.ListRole
import chkan.ua.core.models.isShared
import chkan.ua.domain.models.Item
import chkan.ua.domain.objects.Editable
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.core.analytics.AnalyticsScreenViewEffect
import chkan.ua.shoppinglist.core.analytics.LocalAnalytics
import chkan.ua.shoppinglist.navigation.ItemsRoute
import chkan.ua.shoppinglist.navigation.LocalNavController
import chkan.ua.shoppinglist.session.SessionState
import chkan.ua.shoppinglist.session.SessionViewModel
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.AddItemBottomSheet
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.BottomSheetAction
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.ConfirmBottomSheet
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.EditBottomSheet
import chkan.ua.shoppinglist.ui.kit.empty_state.CenteredTextScreen
import chkan.ua.shoppinglist.ui.kit.items.ItemItem
import chkan.ua.shoppinglist.ui.kit.items.ReadyItem
import chkan.ua.shoppinglist.ui.kit.togglers.ToggleShowCompleted
import chkan.ua.shoppinglist.ui.screens.items.ItemsIntent.*
import chkan.ua.shoppinglist.ui.screens.lists.ListsViewModel
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import chkan.ua.shoppinglist.utils.AppEvent
import chkan.ua.shoppinglist.utils.EventBus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    args: ItemsRoute,
    sessionViewModel: SessionViewModel,
    listsViewModel: ListsViewModel,
    itemsViewModel: ItemsViewModel = hiltViewModel()
) {
    AnalyticsScreenViewEffect("ItemsScreen")
    val context = LocalContext.current
    val navController = LocalNavController.current
    val analytics = LocalAnalytics.current
    val listId = args.listId
    val listTitle = args.listTitle
    val role = args.role

    //confirmShare
    var showConfirmShareBottomSheet by remember { mutableStateOf(false) }
    val confirmShareSheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        if (role.isShared) {
            itemsViewModel.observeRemoteItems(listId)
        } else {
            itemsViewModel.observeItems(listId)
        }
        itemsViewModel.saveLastOpenedList(listId, listTitle, role)
    }

    val eventBus: EventBus = itemsViewModel.eventBus

    LaunchedEffect(Unit) {
        eventBus.events.collect { event ->
            when (event) {
                is AppEvent.SharedSuccess -> {
                    showShareLink(context, event.listId)
                    eventBus.consumeEvent()
                    analytics.logEvent("items_shown_share_link")
                }

                AppEvent.GoToBackAfterUnfollow -> {
                    eventBus.consumeEvent()
                    navController.popBackStack()
                }
                null -> {}
            }
        }
    }

    val sessionState by sessionViewModel.sessionState.collectAsStateWithLifecycle()
    val uiState by itemsViewModel.state.collectAsStateWithLifecycle()
    val isLoading by itemsViewModel.isLoading.collectAsStateWithLifecycle()

    val addItemBottomSheetState by itemsViewModel.addItemBottomSheetState
    val addItemSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showEditBottomSheet by remember { mutableStateOf(false) }
    val editSheetState = rememberModalBottomSheetState()
    var editable by remember { mutableStateOf(Editable()) }
    val scope = rememberCoroutineScope()

    //unfollow list
    var showConfirmUnfollowList by remember { mutableStateOf(false) }
    val confirmUnfollowListState = rememberModalBottomSheetState()

    ItemsScreenContent(
        title = listTitle,
        uiState = uiState,
        isLoading = isLoading,
        sessionState = sessionState,
        context = context,
        handleAddItemSheet = { isShow ->
            itemsViewModel.processAddItemBottomSheetChange(BottomSheetAction.SetIsOpen(isShow))
            if (isShow) {
                scope.launch { addItemSheetState.show() }
            } else {
                scope.launch { addItemSheetState.hide() }
            }
        },
        onDeleteItem = { item -> itemsViewModel.processIntent(ItemsIntent.DeleteItem(item)) },
        onMarkReady = { item, state ->
            itemsViewModel.processIntent(
                ItemsIntent.MarkReady(
                    item,
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
        },
        onShowPaywall = {
            sessionViewModel.showPaywall()
        },
        onUnfollow = {
            showConfirmUnfollowList = true
            scope.launch { confirmUnfollowListState.show() }
        },
        onShowConfirmShare = {
            showConfirmShareBottomSheet = true
            scope.launch { confirmShareSheetState.show() }
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
                        title = addedItem.content.firstAsTitle(),
                        note = addedItem.note
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
            onEdit = { edited -> itemsViewModel.processIntent(EditItem(edited)) },
            editable = editable
        )
    }

    if (showConfirmUnfollowList) {
        ConfirmBottomSheet(
            confirmUnfollowListState,
            question = stringResource(id = R.string.sure_unfollow_list),
            onConfirm = {
                scope.launch {
                    listsViewModel.onUnfollow(listId)
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
    if (showConfirmShareBottomSheet) {
        ConfirmBottomSheet(
            confirmShareSheetState,
            question = stringResource(id = R.string.sure_share_list),
            onConfirm = {
                analytics.logEvent("items_confirm_sharing")
                scope.launch {
                    if (sessionState.isSubscribed == true) {
                        itemsViewModel.processIntent(ShareList(listId))
                        confirmShareSheetState.hide()
                        showConfirmShareBottomSheet = false
                    } else {
                        sessionViewModel.showPaywall()
                    }
                }
            },
            onDismiss = {
                scope.launch {
                    confirmShareSheetState.hide()
                    showConfirmShareBottomSheet = false
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreenContent(
    title: String,
    uiState: ItemsState,
    sessionState: SessionState,
    context: Context,
    handleAddItemSheet: (Boolean) -> Unit,
    onMarkReady: (Item, Boolean) -> Unit,
    onDeleteItem: (Item) -> Unit,
    goToBack: () -> Unit,
    clearReadyItems: () -> Unit,
    onEditItem: (Editable) -> Unit,
    onMoveToTop: (String, Int) -> Unit,
    onShowPaywall: () -> Unit,
    onUnfollow: () -> Unit,
    onShowConfirmShare: () -> Unit,
    isLoading: Boolean,
) {
    var showConfirmBottomSheet by remember { mutableStateOf(false) }
    val confirmSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var isReadyShown by remember { mutableStateOf(false) }
    val analytics = LocalAnalytics.current

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
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    if (uiState.role.isShared){
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp),
                            )
                        } else {
                            Icon(
                                painterResource(R.drawable.ic_share),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                contentDescription = "Share list"
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            when (uiState.role) {
                                ListRole.SHARED_MEMBER -> {
                                    onUnfollow()
                                }

                                ListRole.SHARED_OWNER -> {
                                    if (sessionState.isSubscribed == true) {
                                        showShareLink(context, uiState.listId)
                                        analytics.logEvent("items_shown_share_link")
                                    } else {
                                        onShowPaywall()
                                    }
                                }

                                ListRole.LOCAL -> {
                                    onShowConfirmShare()
                                }
                            }
                        },
                        modifier = Modifier.padding(end = dimensionResource(R.dimen.inner_padding))
                    ) {
                        val resIcon =
                            if (uiState.role == ListRole.SHARED_MEMBER) R.drawable.ic_unfollow else R.drawable.ic_member_add
                        Icon(
                            painterResource(resIcon),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "Share list"
                        )
                    }
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

        LaunchedEffect(uiState.isEmpty) {
            if (uiState.isEmpty) {
                handleAddItemSheet.invoke(true)
            }
        }

        if (uiState.isEmpty) {
            CenteredTextScreen(stringResource(id = R.string.text_empty_items))
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 144.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = paddingValue.calculateTopPadding(),
                        start = dimensionResource(R.dimen.root_padding),
                        end = dimensionResource(R.dimen.root_padding)
                    )
            ) {
                itemsIndexed(
                    uiState.notReadyItems,
                    key = { _, item -> item.itemId }) { index, item ->
                    ItemItem(
                        text = item.content,
                        note = item.note,
                        modifier = Modifier.animateItem(),
                        onReady = { onMarkReady(item, true) },
                        onDelete = { onDeleteItem(item) },
                        onEdit = {
                            onEditItem(
                                Editable(
                                    item.itemId,
                                    item.content,
                                    note = item.note
                                )
                            )
                        },
                        onMoveToTop = { onMoveToTop(item.itemId, item.position) },
                        isFirst = index == 0,
                        isShared = uiState.role.isShared
                    )
                }

                if (uiState.readyItems.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        ToggleShowCompleted(
                            isShowing = isReadyShown,
                            showText = stringResource(id = R.string.show_completed) + " (${uiState.readyItems.size})",
                            hideText = stringResource(id = R.string.hide_completed) + " (${uiState.readyItems.size})",
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
                    items(uiState.readyItems, key = { it.itemId }) { item ->
                        ReadyItem(
                            text = item.content,
                            modifier = Modifier.animateItem(),
                            onNotReady = { onMarkReady.invoke(item, false) },
                            onDeleteItem = { onDeleteItem.invoke(item) })
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

fun showShareLink(context: Context, listId: String) {
    val code = "${listId.first()}${listId.last()}$listId"
    val sharedLink =
        "${context.getString(R.string.join_my_list)} https://colistly.web.app/join?code=$code"
    shareLink(context, sharedLink)
}


fun shareLink(context: Context, link: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, link)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.share_invitation))
    context.startActivity(shareIntent)
}

@Preview(showBackground = true)
@Composable
fun ItemsScreenContentPreview() {
    ShoppingListTheme {
        ItemsScreenContent(
            "Title",
            uiState = ItemsState(),
            sessionState = SessionState(),
            context = LocalContext.current,
            {},
            { _, _ -> },
            {},
            {},
            {},
            {},
            { _, _ -> },
            {},
            {},
            {},
            isLoading = false
        )
    }
}