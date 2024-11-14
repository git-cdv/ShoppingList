package chkan.ua.shoppinglist.ui.screens.items

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chkan.ua.domain.models.Item
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.core.models.MenuItem
import chkan.ua.shoppinglist.navigation.ItemsRoute
import chkan.ua.shoppinglist.ui.kit.BaseDropdownMenu
import chkan.ua.shoppinglist.ui.kit.RoundedTextField
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.launch

@Composable
fun ItemsScreen(
    args: ItemsRoute,
    itemsViewModel: ItemsViewModel = hiltViewModel()
) {
    val items by itemsViewModel.getFlowItemsByListId(args.listId).collectAsStateWithLifecycle(initialValue = listOf())

    ItemsScreenContent(items,
        onDeleteItem = { id ->  },
        addItem = { title -> itemsViewModel.addItem(Item(content = title, listId = args.listId))}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreenContent(
    lists: List<Item>,
    onDeleteItem: (Int) -> Unit,
    addItem: (String) -> Unit
) {
    var listNameText by rememberSaveable { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(lists, key = { it.itemId }) { item ->
                ItemItem(
                    text = item.content,
                    modifier = Modifier.animateItem(),
                    onDeleteList = { onDeleteItem.invoke(item.itemId) })
            }
        }

        FloatingActionButton(
            onClick = {
                showBottomSheet = true
                scope.launch { sheetState.show() }
            },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Открыть BottomSheet")
        }

        if (showBottomSheet){
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = {  scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                } }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    RoundedTextField(
                        value = listNameText,
                        onValueChange = { newText -> listNameText = newText },
                        roundedCornerRes = R.dimen.rounded_corner,
                        placeholderTextRes = R.string.first_list_text_placeholder,
                        onDone = { addItem.invoke(listNameText) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding()
                            .padding(horizontal = dimensionResource(id = R.dimen.root_padding))
                    )
                }
            }
        }
    }
}

@Composable
fun ItemItem(
    text: String,
    modifier: Modifier,
    onDeleteList: () -> Unit)
{
    Card(
        onClick = {},
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)),
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = dimensionResource(id = R.dimen.inner_padding),
                horizontal = dimensionResource(id = R.dimen.root_padding)
            )
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (textTitle, menuIcon) = createRefs()
            var isMenuExpanded by remember { mutableStateOf(false) }

            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(
                        top = dimensionResource(id = R.dimen.root_padding),
                        start = dimensionResource(id = R.dimen.root_padding)
                    )
                    .constrainAs(textTitle) {
                        start.linkTo(parent.start)
                    }
            )

            //box needed to open menu under icon
            Box(modifier = Modifier
                .constrainAs(menuIcon) {
                    top.linkTo(parent.top, 6.dp)
                    end.linkTo(parent.end, 8.dp)
                }){
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)))
                        .clickable { isMenuExpanded = true }
                )

                BaseDropdownMenu(
                    isMenuExpanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                    listItems = listOf(
                        MenuItem(title = stringResource(id = R.string.delete), onClick = { onDeleteList.invoke()}),
                        MenuItem(title = stringResource(id = R.string.edit), onClick = { }),
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemsScreenContentPreview() {
    ShoppingListTheme {
        ItemsScreenContent(listOf(
            Item(333,"Item 1", 0,0, false),
            Item(444,"Item 2", 0,1, false),
            Item(555,"Item 3", 0,2, false)
        ),
            {}, {})
    }
}