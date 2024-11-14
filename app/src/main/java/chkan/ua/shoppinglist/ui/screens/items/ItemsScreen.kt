package chkan.ua.shoppinglist.ui.screens.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chkan.ua.domain.models.Item
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.navigation.ItemsRoute
import chkan.ua.shoppinglist.ui.kit.AddItemBottomSheet
import chkan.ua.shoppinglist.ui.kit.items.ItemItem
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.launch

@Composable
fun ItemsScreen(
    args: ItemsRoute,
    itemsViewModel: ItemsViewModel = hiltViewModel()
) {
    val items by itemsViewModel.getFlowItemsByListId(args.listId).collectAsStateWithLifecycle(initialValue = listOf())

    ItemsScreenContent(items,
        onDeleteItem = { id -> itemsViewModel.deleteItem(id) },
        addItem = { title -> itemsViewModel.addItem(Item(content = title, listId = args.listId))}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreenContent(
    lists: List<Item>,
    onDeleteItem: (Int) -> Unit,
    addItem: (String) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Box {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(lists, key = { it.itemId }) { item ->
                ItemItem(
                    text = item.content,
                    modifier = Modifier.animateItem(),
                    onDeleteList = { onDeleteItem.invoke(item.itemId) })
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

@Preview(showBackground = true)
@Composable
fun ItemsScreenContentPreview() {
    ShoppingListTheme {
        ItemsScreenContent(listOf(
            Item(333,"Item 1", 0,0, false),
            Item(444,"Item 2", 0,1, false),
            Item(555,"Item 3", 0,2, false)
        ),
            {}, {})
    }
}