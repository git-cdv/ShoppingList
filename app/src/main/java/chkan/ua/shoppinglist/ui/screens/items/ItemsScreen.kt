package chkan.ua.shoppinglist.ui.screens.items

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import chkan.ua.shoppinglist.navigation.ItemsRoute
import chkan.ua.shoppinglist.ui.screens.lists.ListsViewModel

@Composable
fun ItemsScreen(args: ItemsRoute, listsViewModel: ListsViewModel = hiltViewModel()) {
    val items = listsViewModel.getFlowItemsByListId(args.listId)

    Box(modifier = Modifier.fillMaxSize()){
        Text(text = "ItemsScreen - ${args.listId}", modifier = Modifier.align(Alignment.Center))
    }
}