package chkan.ua.shoppinglist.ui.screens.items

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import chkan.ua.shoppinglist.navigation.ItemsRoute

@Composable
fun ItemsScreen(args: ItemsRoute) {
    Box(modifier = Modifier.fillMaxSize()){
        Text(text = "ItemsScreen - ${args.listId}", modifier = Modifier.align(Alignment.Center))
    }
}