package chkan.ua.shoppinglist.components.history_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import chkan.ua.domain.models.HistoryItem
import chkan.ua.shoppinglist.core.components.StateComponent

@Composable
fun HistoryUiComponent(component: StateComponent<HistoryComponentState>) {
    val state by component.stateFlow.collectAsState()
    HistoryComponentContent(state)
}

@Composable
fun HistoryComponentContent(state: HistoryComponentState) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize=120.dp),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ){
        items(state.list, key = { it.id }) { item ->
            Column(Modifier.padding(7.dp), horizontalAlignment = Alignment.CenterHorizontally){
                Text(item.name)
            }
        }
    }
}

@Preview
@Composable
private fun ComponentPreview() {
    val state = HistoryComponentState(isShow = true, isShort = true, list = listOf(
        HistoryItem(1,"Product 1"),
        HistoryItem(2,"Product 2"),
        HistoryItem(3,"Product 3"),
        HistoryItem(4,"Product 4"),
        HistoryItem(5,"Product 5"))
    )
    HistoryComponentContent(state= state)
}

