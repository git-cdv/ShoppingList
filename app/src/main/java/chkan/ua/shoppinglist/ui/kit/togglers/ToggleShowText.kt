package chkan.ua.shoppinglist.ui.kit.togglers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun ToggleShowText(isShowing: Boolean, showText: String, hideText: String, onToggle: (Boolean)->Unit, modifier: Modifier){
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End) {
        Icon(
            imageVector = if (isShowing) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp ,
            contentDescription = "Drag handle",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.CenterVertically)
        )
        Text(text = if (isShowing) hideText else showText ,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)))
                .clickable { onToggle.invoke(!isShowing) }
                .padding(horizontal = dimensionResource(id = R.dimen.inner_padding))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ToggleShowTextPreview() {
    ShoppingListTheme {
        ToggleShowText(false, "Show","Hide",{},Modifier.fillMaxWidth())
    }
}