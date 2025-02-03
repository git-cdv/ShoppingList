package chkan.ua.shoppinglist.ui.kit.items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import chkan.ua.shoppinglist.R

@Composable
fun SuggestionListCard(suggestion: String, onSuggestionChoose: (String) -> Unit) {
    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(4.dp)
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)))
            .clickable { onSuggestionChoose.invoke(suggestion) },
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Text(text = suggestion,
            color = Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}