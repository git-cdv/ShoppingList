package chkan.ua.shoppinglist.ui.kit.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import chkan.ua.core.extensions.firstAsTitle
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.core.models.MenuItem
import chkan.ua.shoppinglist.ui.kit.BaseDropdownMenu
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun ItemItem(
    text: String,
    modifier: Modifier,
    onReady: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        onClick = { onReady.invoke() },
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)),
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = dimensionResource(id = R.dimen.min_padding),
                horizontal = dimensionResource(id = R.dimen.root_padding)
            )
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (textTitle, menuIcon) = createRefs()
            var isMenuExpanded by remember { mutableStateOf(false) }

            Text(
                text = text.firstAsTitle(),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(vertical = dimensionResource(id = R.dimen.min_padding))
                    .constrainAs(textTitle) {
                        start.linkTo(parent.start, 16.dp)
                        end.linkTo(menuIcon.start, 8.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }

            )

            //box needed to open menu under icon
            Box(modifier = Modifier
                .constrainAs(menuIcon) {
                    end.linkTo(parent.end)
                }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = Color.Gray,
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)))
                        .clickable { isMenuExpanded = true }
                )

                BaseDropdownMenu(
                    isMenuExpanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                    listItems = listOf(
                        MenuItem(
                            title = stringResource(id = R.string.delete),
                            onClick = { onDelete.invoke() }),
                        MenuItem(
                            title = stringResource(id = R.string.edit),
                            onClick = { onEdit.invoke() }),
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemPreview() {
    ShoppingListTheme {
        ItemItem("Products",Modifier,{},{},{})
    }
}