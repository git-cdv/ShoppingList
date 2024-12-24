package chkan.ua.shoppinglist.ui.screens.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chkan.ua.domain.models.Item
import chkan.ua.domain.models.ListItemsUi
import chkan.ua.domain.models.ListProgress
import chkan.ua.domain.usecases.lists.MoveTop
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.navigation.ItemsRoute
import chkan.ua.shoppinglist.navigation.localNavController
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.AddListBottomSheet
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.ConfirmBottomSheet
import chkan.ua.shoppinglist.ui.kit.items.ListItem
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.launch

@Composable
fun ListsScreen(
    listsViewModel: ListsViewModel = hiltViewModel()
){
    val navController = localNavController.current
    val lists by listsViewModel.listsFlow.collectAsStateWithLifecycle(initialValue = listOf())

    LaunchedEffect(Unit) {
        listsViewModel.clearLastOpenedList()
    }

    ListsScreenContent(lists,
        onDeleteList = { id -> listsViewModel.deleteList(id) },
        onCreateList = { title -> listsViewModel.addList(title) },
        onMoveToTop = { id, position -> listsViewModel.moveToTop(MoveTop(id, position)) },
        goToItems = { list -> navController.navigate(ItemsRoute(list.id, list.title)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreenContent(
    lists: List<ListItemsUi>,
    onDeleteList: (Int) -> Unit,
    onCreateList: (String) -> Unit,
    onMoveToTop: (Int, Int) -> Unit,
    goToItems: (ListItemsUi) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showConfirmBottomSheet by remember { mutableStateOf(false) }
    val confirmSheetState = rememberModalBottomSheetState()
    var argDeletedIdList by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.lists), color = Color.Gray) },
                scrollBehavior = scrollBehavior)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showBottomSheet = true
                    scope.launch { sheetState.show() }
                },
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.root_padding))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add List")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValue ->

        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(top = paddingValue.calculateTopPadding())
                .background(MaterialTheme.colorScheme.background)
        ){
            items(lists, key = { it.id }){ list ->
                ListItem(
                    list = list,
                    modifier = Modifier.animateItem(),
                    onDeleteList = {
                        argDeletedIdList = list.id
                        showConfirmBottomSheet = true
                        scope.launch { confirmSheetState.show() }
                                   },
                    onMoveToTop = { onMoveToTop.invoke(list.id, list.position) },
                    onCardClick = { goToItems.invoke(list) }
                )
            }
        }

        if (showBottomSheet){
            AddListBottomSheet(sheetState,
                onDismiss = { showBottomSheet = false },
                addItem = { text -> onCreateList.invoke(text)},
                R.string.first_list_text_placeholder)
        }

        if (showConfirmBottomSheet){
            ConfirmBottomSheet(
                confirmSheetState,
                question = stringResource(id = R.string.sure_delete_list),
                onConfirm = { scope.launch {
                    onDeleteList.invoke(argDeletedIdList)
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
fun ListsScreenContentPreview() {
    ShoppingListTheme {
        ListsScreenContent(listOf(ListItemsUi(
            id = 6187,
            title = "Commodo",
            position = 1,
            count = 4,
            readyCount = 2,
            items = listOf(Item(
                itemId = 5847,
                content = "senserit",
                listId = 8123,
                position = 75556,
                isReady = false
            )), progress = ListProgress(count = 4, readyCount = 2)
        )),{},{},{_,_->},{})
    }
}