package chkan.ua.shoppinglist.ui.screens.items

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ItemsScreen (){
    Box(modifier = Modifier.fillMaxSize()){
        Text(text = "ItemsScreen", modifier = Modifier.align(Alignment.Center))
    }
}