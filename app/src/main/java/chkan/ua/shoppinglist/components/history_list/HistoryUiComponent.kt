package chkan.ua.shoppinglist.components.history_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import chkan.ua.shoppinglist.core.components.Component
import chkan.ua.shoppinglist.core.components.StateComponent
import chkan.ua.shoppinglist.ui.kit.items.ItemItem

@Composable
fun HistoryUiComponent(component: StateComponent<HistoryComponentState>) {
    val state by component.stateFlow.collectAsState()
    HistoryComponentContent(state)
}

@Composable
fun HistoryComponentContent(state: HistoryComponentState) {

    LazyColumn(
        contentPadding = PaddingValues(bottom = 64.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        /*items(items, key = { it.itemId }) { item ->
            ItemItem(
                text = item.content,
                modifier = Modifier.animateItem(),
                onReady = { onMarkReady.invoke(item.itemId, true) },
                onDeleteList = { onDeleteItem.invoke(item.itemId) })
        }*/
    }
}

@Preview
@Composable
private fun ComponentPreview() {
    val state = HistoryComponentState(isShow = true, isShort = true, list = listOf())
    HistoryComponentContent(state= state)
}

