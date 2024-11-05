package chkan.ua.shoppinglist.ui.kit

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.launch

@Composable
fun SuggestionsHorizontalList(suggestions: List<String>, onSuggestionChoose: (String) -> Unit, modifier: Modifier){
    val animatedOffsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            animatedOffsetX.animateTo(
                targetValue = 20f,
                animationSpec = tween(durationMillis = 300)
            )
            animatedOffsetX.animateTo(
                targetValue = -20f,
                animationSpec = tween(durationMillis = 300)
            )
            animatedOffsetX.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 300)
            )
        }
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.horizontal_root_padding)),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.offset(x = animatedOffsetX.value.dp)
    ) {
        items(suggestions) { suggestion ->
            SuggestionItemCard(suggestion,onSuggestionChoose)
        }
    }
}

@Composable
fun SuggestionItemCard(suggestion: String, onSuggestionChoose: (String) -> Unit) {
    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(4.dp)
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)))
            .clickable { onSuggestionChoose.invoke(suggestion) },
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)),
        border = BorderStroke(1.dp,Color.Gray)
    ) {
        Text(text = suggestion,
            color = Color.Gray,
            modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SuggestionsHorizontalListPreview() {
    ShoppingListTheme {
        SuggestionsHorizontalList(listOf("Products", "Today"), {}, modifier = Modifier.fillMaxWidth())
    }
}