package chkan.ua.shoppinglist.ui.kit.togglers

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun ToggleShowText(isHide: Boolean){
    Row {
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Drag handle",
            tint = Color.Gray,
            modifier = Modifier
                .padding(start = dimensionResource(id = R.dimen.root_padding), end = dimensionResource(id = R.dimen.min_padding))
                .size(20.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ToggleShowTextPreview() {
    ShoppingListTheme {
        ToggleShowText(false)
    }
}