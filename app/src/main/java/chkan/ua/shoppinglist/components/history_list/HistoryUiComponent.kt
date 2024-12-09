package chkan.ua.shoppinglist.components.history_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import chkan.ua.domain.models.HistoryItem
import chkan.ua.shoppinglist.core.components.StateComponent
import chkan.ua.shoppinglist.ui.kit.items.SuggestionItemCard

@Composable
fun HistoryUiComponent(component: StateComponent<HistoryComponentState>) {
    val state by component.stateFlow.collectAsState()
    HistoryComponentContent(state)
}

@Composable
fun HistoryComponentContent(state: HistoryComponentState) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize=100.dp),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Absolute.Left
    ){
        items(state.list, key = { it.id }) { item ->
            SuggestionItemCard(item.name){}
        }
    }
}

@Preview
@Composable
private fun ComponentPreview() {
    val state = HistoryComponentState(isShow = true, isShort = true, list = listOf(
        HistoryItem(1,"Product1"),
        HistoryItem(2,"Product2"),
        HistoryItem(3,"Product 3555"),
        HistoryItem(4,"Product 4"),
        HistoryItem(5,"Product nijioj"))
    )
    HistoryComponentContent(state= state)
}

