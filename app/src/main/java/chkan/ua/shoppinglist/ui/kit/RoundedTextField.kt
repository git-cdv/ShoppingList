package chkan.ua.shoppinglist.ui.kit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun RoundedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    roundedCornerRes: Int,
    placeholderTextRes: Int,
    modifier: Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(dimensionResource(id = roundedCornerRes)),
        label = null,
        placeholder = { Text(stringResource(id = placeholderTextRes), color = Color.Gray) },
        maxLines = 2,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun RoundedTextFieldPreview() {
    ShoppingListTheme {
        RoundedTextField(
            value = "",
            onValueChange ={},
            roundedCornerRes = R.dimen.rounded_corner,
            placeholderTextRes = R.string.first_list_text_placeholder,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
