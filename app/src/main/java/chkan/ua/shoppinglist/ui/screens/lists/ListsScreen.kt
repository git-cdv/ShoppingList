package chkan.ua.shoppinglist.ui.screens.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun ListsScreen(){
    ListsScreenContent()
}

@Composable
fun ListsScreenContent(
    listsViewModel: ListsViewModel = hiltViewModel()
) {

    val lists by listsViewModel.listsFlow.collectAsStateWithLifecycle(
        initialValue = listOf()
    )

    LazyColumn(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ){
        items(lists, key = {it.id}){ list ->
            ListItem(text = list.title, modifier = Modifier.animateItem()){
                //listsViewModel.deleteList(list.id)
            }
        }
    }
}

@Composable
fun ListItem(
    text: String,
    modifier: Modifier,
    onCardClick: () -> Unit){
    Card(
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)),
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = dimensionResource(id = R.dimen.inner_padding),
                horizontal = dimensionResource(id = R.dimen.root_padding)
            )
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (textTitle, textCounter, progress, menuIcon) = createRefs()

            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.root_padding), start = dimensionResource(id = R.dimen.root_padding))
                    .constrainAs(textTitle) {
                        start.linkTo(parent.start)
                    }
            )

            Text(
                text = "1/4",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .constrainAs(textCounter) {
                        start.linkTo(menuIcon.start)
                        end.linkTo(menuIcon.end)
                        top.linkTo(progress.top)
                        bottom.linkTo(progress.bottom)
                    }
            )

            LinearProgressIndicator(
                progress = { 0.5f } ,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.secondaryContainer,
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

            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .constrainAs(menuIcon) {
                        top.linkTo(parent.top, 6.dp)
                        end.linkTo(parent.end, 8.dp)
                    }
                    .padding(6.dp)
                    .clip(RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)))
                    .clickable { onCardClick.invoke() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListItemPreview() {
    ShoppingListTheme {
        ListItem(text = "Main List", modifier = Modifier) {}
    }
}