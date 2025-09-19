package chkan.ua.shoppinglist.ui.kit.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import chkan.ua.core.models.ListRole
import chkan.ua.core.models.isShared
import chkan.ua.domain.models.ListItemsUi
import chkan.ua.domain.models.ListProgress
import chkan.ua.domain.objects.Editable
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.core.models.MenuItem
import chkan.ua.shoppinglist.ui.kit.BaseDropdownMenu
import chkan.ua.shoppinglist.ui.screens.lists.ListUiEvent
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun ListItem(
    list: ListItemsUi,
    modifier: Modifier,
    onListEvent: (ListUiEvent) -> Unit,
    isFirst: Boolean
) {
    Card(
        onClick = { onListEvent(ListUiEvent.OnCardClick(list)) },
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)),
        modifier = modifier
            .fillMaxWidth()
            .padding(
                bottom = dimensionResource(id = R.dimen.inner_padding)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (textTitle, textCounter, progress, menuIcon) = createRefs()
            var isMenuExpanded by remember { mutableStateOf(false) }

            Text(
                text = list.title,
                style = MaterialTheme.typography.titleMedium,
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

            Text(
                text = "${list.readyCount}/${list.count}",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .constrainAs(textCounter) {
                        start.linkTo(menuIcon.start)
                        end.linkTo(menuIcon.end)
                        top.linkTo(progress.top)
                        bottom.linkTo(progress.bottom)
                    }
            )

            LinearProgressIndicator(
                progress = { list.progress.get() } ,
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .height(dimensionResource(id = R.dimen.height_progress))
                    .constrainAs(progress) {
                        start.linkTo(parent.start, 16.dp)
                        top.linkTo(textTitle.bottom, 16.dp)
                        end.linkTo(textCounter.start, 16.dp)
                        bottom.linkTo(parent.bottom, 16.dp)
                        width = Dimension.fillToConstraints
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
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)))
                        .clickable { isMenuExpanded = true }
                )

                BaseDropdownMenu(
                    isMenuExpanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                    listItems = getMenuItems(
                        role = list.role,
                        list = list,
                        onListEvent = onListEvent,
                        isFirst = isFirst
                    )
                )
            }
        }
    }
}

@Composable
fun getMenuItems(
    role: ListRole,
    list: ListItemsUi,
    onListEvent: (ListUiEvent) -> Unit,
    isFirst: Boolean,
): List<MenuItem> {
    return when (role) {
        ListRole.LOCAL -> {
            mutableListOf<MenuItem>().apply {
                if (!isFirst){
                    add(MenuItem(title = stringResource(id = R.string.moveToTop), onClick = { onListEvent(ListUiEvent.OnMoveToTop(list.id,list.position)) }))
                }
                add(MenuItem(title = stringResource(id = R.string.edit), onClick = { onListEvent(ListUiEvent.OnEditList(Editable(list.id, list.title, isShared = role.isShared))) }))
                add(MenuItem(title = stringResource(id = R.string.delete), onClick = { onListEvent(ListUiEvent.OnDeleteList(list.id,  role.isShared))}))
                add(MenuItem(title = stringResource(id = R.string.share_list), onClick = { onListEvent(ListUiEvent.OnShareList(list.id))}))
            }
        }
        ListRole.SHARED_OWNER -> {
            mutableListOf<MenuItem>().apply {
                add(MenuItem(title = stringResource(id = R.string.edit), onClick = { onListEvent(ListUiEvent.OnEditList(Editable(list.id, list.title, isShared = role.isShared))) }))
                add(MenuItem(title = stringResource(id = R.string.delete), onClick = { onListEvent(ListUiEvent.OnDeleteList(list.id,  role.isShared))}))
                add(MenuItem(title = stringResource(id = R.string.stop_sharing), onClick = { onListEvent(ListUiEvent.OnStopSharing(list.id))}))
            }
        }
        ListRole.SHARED_MEMBER -> {
            mutableListOf<MenuItem>().apply {
                add(MenuItem(title = stringResource(id = R.string.unfollow), onClick = { onListEvent(ListUiEvent.OnStopFollowing(list.id))}))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListItemPreview() {
    ShoppingListTheme {
        ListItem(ListItemsUi(
            id = "2138",
            title = "Item",
            position = 2,
            count = 5,
            readyCount = 2,
            progress = ListProgress(count = 5, readyCount = 2),
            role = ListRole.LOCAL,
        ), Modifier,{}, false)
    }
}