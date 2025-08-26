package chkan.ua.shoppinglist.components.history_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.kit.items.SuggestionItemCard
import chkan.ua.shoppinglist.ui.kit.togglers.ToggleCard
import chkan.ua.shoppinglist.ui.kit.togglers.ToggleShowText
import chkan.ua.shoppinglist.ui.screens.items.ItemsViewModel

@Composable
fun HistoryUiComponent(
    listId: String,
    modifier: Modifier,
    onChoose: (String) -> Unit,
    itemsViewModel: ItemsViewModel = hiltViewModel()
) {
    val component = remember { itemsViewModel.getHistoryComponent(listId) }
    val state by component.stateFlow.collectAsStateWithLifecycle()

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
    onChoose: (String) -> Unit,
    onToggleSize: (Boolean) -> Unit,
    onToggleShow: (Boolean) -> Unit
) {
    Column {
        ToggleShowText(
            isShowing = state.isShow,
            showText = stringResource(id = R.string.show_suggestions),
            hideText = stringResource(id = R.string.hide_suggestions),
            onToggle = {
                onToggleShow.invoke(it)
            },
            modifier = Modifier.fillMaxWidth().padding(end = dimensionResource(id = R.dimen.root_padding), bottom = dimensionResource(id = R.dimen.inner_padding))
        )
        AnimatedVisibility(visible = state.isShow) {
            if (state.list.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.no_suggestions_yet),
                        color = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(
                                vertical = dimensionResource(id = R.dimen.root_padding)
                            )
                    )
                }
            } else {
                val maxHistoryHeight = calculateMaxHistoryHeight()

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = modifier
                        .fillMaxWidth()
                        .heightIn(max = maxHistoryHeight)
                        .animateContentSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalArrangement = Arrangement.Start
                ) {
                    val list = if (state.isShort) {
                        if (state.list.size >= 9) {
                            state.list.take(8)
                        } else state.list
                    } else state.list

                    items(list, key = { it.id }) { item ->
                        SuggestionItemCard(item.name) { onChoose.invoke(item.name) }
                    }

                    if (state.list.size >= 9) {
                        if (state.isShort) {
                            item { ToggleCard(R.string.more) { onToggleSize.invoke(false) } }
                        } else {
                            item { ToggleCard(R.string.less) { onToggleSize.invoke(true) } }
                        }
                    }
                }
            }
        }
    }

}

@Preview
@Composable
private fun ComponentPreview() {
    val state = HistoryComponentState(isShow = true, isShort = true, list = listOf())
    HistoryComponentContent(state = state, modifier = Modifier, {}, {}, {})
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

