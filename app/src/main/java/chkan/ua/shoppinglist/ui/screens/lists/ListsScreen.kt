package chkan.ua.shoppinglist.ui.screens.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chkan.ua.shoppinglist.R

@Composable
fun ListsScreen(){
    ListsScreenContent()
}

@Composable
fun ListsScreenContent(
    listsViewModel: ListsViewModel = hiltViewModel()
) {

    val lists by listsViewModel.listsFlow.collectAsStateWithLifecycle(
        initialValue = listOf()
    )

    LazyColumn(
        Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)
    ){
        items(lists, key = {it.id}){ list ->
            ListItem(text = list.title){
                listsViewModel.deleteList(list.id)
            }
        }
    }
}

@Composable
fun ListItem(text: String, onCardClick: () -> Unit){
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)),
        onClick = {onCardClick.invoke()},
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = dimensionResource(id = R.dimen.vertical_inner_padding),
                horizontal = dimensionResource(id = R.dimen.vertical_outer_padding)
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(16.dp))
    }
}