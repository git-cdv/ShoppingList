package chkan.ua.shoppinglist.ui.screens.first_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import chkan.ua.shoppinglist.navigation.ItemsRoute
import chkan.ua.shoppinglist.navigation.localNavController
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun FirstListScreen(){
    val navController = localNavController.current
    FirstListContent{
        navController.navigate(ItemsRoute)
    }
}

@Composable
fun FirstListContent(navigateToItems: ()-> Unit){
    Box(modifier = Modifier.fillMaxSize()){
        Text(text = "FirstListScreen", modifier = Modifier.align(Alignment.Center))
        FloatingActionButton(
            onClick = { navigateToItems.invoke() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FirstListPreview() {
    ShoppingListTheme {
        FirstListContent {}
    }
}