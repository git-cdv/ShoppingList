package chkan.ua.shoppinglist.ui.screens.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chkan.ua.domain.models.Item
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.navigation.ItemsRoute
import chkan.ua.shoppinglist.ui.kit.AddItemBottomSheet
import chkan.ua.shoppinglist.ui.kit.TopBar
import chkan.ua.shoppinglist.ui.kit.items.ItemItem
import chkan.ua.shoppinglist.ui.kit.items.ReadyItem
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.launch

@Composable
fun ItemsScreen(
    args: ItemsRoute,
    itemsViewModel: ItemsViewModel = hiltViewModel()
) {
    val listId = args.listId
    val listTitle = args.listTitle
    val items by itemsViewModel.getFlowItemsByListId(listId).collectAsStateWithLifecycle(initialValue = listOf())
    val (readyItems, notReadyItems) = items.partition { it.isReady }


    ItemsScreenContent(listTitle, notReadyItems,readyItems,
        onDeleteItem = { id -> itemsViewModel.deleteItem(id) },
        addItem = { title -> itemsViewModel.addItem(Item(content = title, listId = listId))},
        onMarkReady = { id, state -> itemsViewModel.changeReadyInItem(id, state) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreenContent(
    title: String,
    items: List<Item>,
    readyItems: List<Item>,
    onMarkReady: (Int, Boolean) -> Unit,
    onDeleteItem: (Int) -> Unit,
    addItem: (String) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(title = title, onBackClick = { })
        Box {
            LazyColumn(
                Modifier
                    .fillMaxSize()
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

                    items(readyItems, key = { it.itemId }) { item ->
                        ReadyItem(
                            text = item.content,
                            modifier = Modifier.animateItem(),
                            onNotReady = { onMarkReady.invoke(item.itemId, false) },
                            onDeleteItem = { onDeleteItem.invoke(item.itemId) })
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    showBottomSheet = true
                    scope.launch { sheetState.show() }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(dimensionResource(id = R.dimen.root_padding))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }

            if (showBottomSheet){
                AddItemBottomSheet(sheetState,
                    onDismiss = { showBottomSheet = false },
                    addItem = { text -> addItem.invoke(text)})
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemsScreenContentPreview() {
    ShoppingListTheme {
        ItemsScreenContent("Title", listOf(
            Item(333,"Item 1", 0,0, false),
            Item(444,"Item 2", 0,1, false)
        ), listOf(
            Item(55,"Item 1", 0,0, false),
            Item(44774,"Item 2", 0,1, false)
        ),
            {_,_ -> }, {}, {})
    }
}