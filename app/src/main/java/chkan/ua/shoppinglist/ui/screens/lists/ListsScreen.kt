package chkan.ua.shoppinglist.ui.screens.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chkan.ua.domain.models.Item
import chkan.ua.domain.models.ListItems
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.core.models.MenuItem
import chkan.ua.shoppinglist.navigation.ItemsRoute
import chkan.ua.shoppinglist.navigation.localNavController
import chkan.ua.shoppinglist.ui.kit.BaseDropdownMenu
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
    lists: List<ListItems>,
    onDeleteList: (Int) -> Unit,
    goToItems: (ListItems) -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.lists), color = Color.Gray) })
        }
    ) { paddingValue ->

        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(paddingValue)
                .background(MaterialTheme.colorScheme.background)
        ){
            items(lists, key = {it.id}){ list ->
                ListItem(
                    text = list.title,
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
        ListsScreenContent(listOf(ListItems(
            id = 6187,
            title = "Commodo",
            position = 1,
            items = listOf(Item(
                itemId = 5847,
                content = "senserit",
                listId = 8123,
                position = 7555,
                isReady = false
            ))
        )),{},{})
    }
}