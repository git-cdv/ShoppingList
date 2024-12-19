package chkan.ua.shoppinglist.components.history_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import chkan.ua.domain.models.HistoryItem
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.StubHistoryComponent
import chkan.ua.shoppinglist.ui.kit.togglers.ToggleCard
import chkan.ua.shoppinglist.ui.kit.items.SuggestionItemCard
import chkan.ua.shoppinglist.ui.kit.togglers.ToggleShowText

@Composable
fun HistoryUiComponent(component: StubHistoryComponent, modifier: Modifier, onChoose: (String)-> Unit) {
    val state by component.stateFlow.collectAsState()
    HistoryComponentContent(
        state,
        modifier,
        onChoose = onChoose,
        onToggleSize = { component.updateState { copy(isShort = it) } },
        onToggleShow = { component.updateState { copy(isShow = it) } }
    )
}

@Composable
fun HistoryComponentContent(
    state: HistoryComponentState,
    modifier: Modifier,
    onChoose: (String)-> Unit,
    onToggleSize: (Boolean)-> Unit,
    onToggleShow: (Boolean)-> Unit) {

    Column {
        ToggleShowText(state.isShow){ onToggleShow.invoke(it)}
        AnimatedVisibility(visible = state.isShow) {
            val maxHistoryHeight = calculateMaxHistoryHeight()

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(max = maxHistoryHeight)
                    .animateContentSize(),
                verticalArrangement = Arrangement.Top,
                horizontalArrangement = Arrangement.Start
            ){
                val list = if (state.isShort) {
                    if (state.list.size >= 9){
                        state.list.take(8)
                    } else state.list
                } else state.list

                items(list, key = { it.id }) { item ->
                    SuggestionItemCard(item.name){ onChoose.invoke(item.name) }
                }

                if (state.list.size >= 9){
                    if (state.isShort){
                        item { ToggleCard(R.string.more){ onToggleSize.invoke(false) } }
                    } else {
                        item { ToggleCard(R.string.less){ onToggleSize.invoke(true) } }
                    }
                }
            }
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
    HistoryComponentContent(state= state, modifier = Modifier,{},{},{})
}

const val DEFAULT_TEXT_FIELD_HEIGHT = 56

@Composable
fun calculateMaxHistoryHeight(): Dp {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val dragHandlerHeightAndBottomPadding = 28.dp
    val toggleShowTextHeight = 24.dp
    val keyboardHeight = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    return screenHeight - keyboardHeight - dragHandlerHeightAndBottomPadding - toggleShowTextHeight - DEFAULT_TEXT_FIELD_HEIGHT.dp
}

