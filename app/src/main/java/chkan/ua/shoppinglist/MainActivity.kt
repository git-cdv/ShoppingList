package chkan.ua.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import chkan.ua.shoppinglist.navigation.NavigationContainer
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoppingListTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationContainer(innerPadding)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartPreview() {
    ShoppingListTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavigationContainer(innerPadding)
        }
    }
}