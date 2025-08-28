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
import chkan.ua.domain.models.ListItemsUi
import chkan.ua.domain.models.ListProgress
import chkan.ua.domain.objects.Editable
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.core.models.MenuItem
import chkan.ua.shoppinglist.ui.kit.BaseDropdownMenu
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun ListItem(
    list: ListItemsUi,
    modifier: Modifier,
    onEditList: (Editable) -> Unit,
    onDeleteList: () -> Unit,
    onMoveToTop: () -> Unit,
    onCardClick: () -> Unit,
    isFirst: Boolean)
{
    Card(
        onClick = { onCardClick.invoke() },
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
                    listItems = mutableListOf<MenuItem>().apply {
                        if (!isFirst && !list.isShared){
                            add(MenuItem(title = stringResource(id = R.string.moveToTop), onClick = { onMoveToTop.invoke() }))
                        }
                        add(MenuItem(title = stringResource(id = R.string.edit), onClick = { onEditList.invoke(Editable(list.id, list.title, isShared = list.isShared)) }))
                        add(MenuItem(title = stringResource(id = R.string.delete), onClick = { onDeleteList.invoke()}))
                    }
                )
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
            isShared = false
        ), Modifier,{},{},{},{}, false)
    }
}