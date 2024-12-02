package chkan.ua.shoppinglist.components.history_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import chkan.ua.shoppinglist.core.components.Component
import chkan.ua.shoppinglist.core.components.StateComponent

@Composable
fun HistoryUiComponent(component: StateComponent<HistoryComponentState>) {
    val state by component.stateFlow.collectAsState()
    HistoryComponentContent(state)
}

@Composable
fun HistoryComponentContent(state: HistoryComponentState) {

}

@Preview
@Composable
private fun ComponentPreview() {
    val state = HistoryComponentState(isShow = true, isShort = true, list = listOf())
    HistoryComponentContent(state= state)
}

