package chkan.ua.shoppinglist.ui.kit.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun ReadyItem(
    text: String,
    modifier: Modifier,
    onNotReady: () -> Unit,
    onDeleteItem: () -> Unit)
{
    Row(modifier = modifier.clickable { onNotReady.invoke() }) {
        Icon(
            painter = painterResource(R.drawable.icon_restore),
            contentDescription = "Drag handle",
            tint = Color.Gray,
            modifier = Modifier
                .padding(start = dimensionResource(id = R.dimen.root_padding), end = dimensionResource(id = R.dimen.min_padding))
                .size(20.dp)
                .align(Alignment.CenterVertically)
        )
        ConstraintLayout(modifier = Modifier.fillMaxWidth().padding(
            top = dimensionResource(id = R.dimen.min_padding),
            bottom = dimensionResource(id = R.dimen.min_padding),
            end = dimensionResource(id = R.dimen.root_padding)
        )) {
            val (textTitle, menuIcon) = createRefs()

            Text(
                text = text.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                style = MaterialTheme.typography.titleMedium.copy(
                    textDecoration = TextDecoration.LineThrough
                ),
                textAlign = TextAlign.Start,
                color = Color.Gray,
                modifier = Modifier
                    .padding(vertical = dimensionResource(id = R.dimen.min_padding))
                    .constrainAs(textTitle) {
                        start.linkTo(parent.start, 16.dp)
                        end.linkTo(menuIcon.start,8.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }

            )

            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete ready item",
                tint = Color.Gray,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)))
                    .clickable { onDeleteItem.invoke() }
                    .constrainAs(menuIcon) {
                        end.linkTo(parent.end, 4.dp)
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReadyItemPreview() {
    ShoppingListTheme {
        ReadyItem("Products",Modifier,{},{})
    }
}