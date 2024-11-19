package chkan.ua.shoppinglist.ui.screens.lists

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chkan.ua.domain.models.Item
import chkan.ua.domain.models.ListItemsUi
import chkan.ua.domain.models.ListProgress
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.navigation.ItemsRoute
import chkan.ua.shoppinglist.navigation.localNavController
import chkan.ua.shoppinglist.ui.kit.items.ListItem
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun ListsScreen(
    listsViewModel: ListsViewModel = hiltViewModel()
){
    val navController = localNavController.current
    val lists by listsViewModel.listsFlow.collectAsStateWithLifecycle(initialValue = listOf())

    ListsScreenContent(lists,
        onDeleteList = { id -> listsViewModel.deleteList(id) },
        goToItems = {list -> navController.navigate(ItemsRoute(list.id, list.title))})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreenContent(
    lists: List<ListItemsUi>,
    onDeleteList: (Int) -> Unit,
    goToItems: (ListItemsUi) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.lists), color = Color.Gray) },
                scrollBehavior = scrollBehavior)
        }
    ) { paddingValue ->

        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(top = paddingValue.calculateTopPadding())
                .background(MaterialTheme.colorScheme.background)
        ){
            items(lists, key = {it.id}){ list ->
                Log.d("CHKAN", "list progress:${list.progress.get()} ")
                ListItem(
                    list = list,
                    modifier = Modifier.animateItem(),
                    onDeleteList = { onDeleteList.invoke(list.id) },
                    onCardClick = {goToItems.invoke(list)} )
            }
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
                position = 7555,
                isReady = false
            )), progress = ListProgress(count = 4, readyCount = 2)
        )),{},{})
    }
}