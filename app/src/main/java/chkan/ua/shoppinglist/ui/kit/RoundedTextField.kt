package chkan.ua.shoppinglist.ui.kit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun RoundedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    roundedCornerRes: Int,
    placeholderTextRes: Int,
    onDone: () -> Unit,
    modifier: Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(dimensionResource(id = roundedCornerRes)),
        label = null,
        placeholder = { Text(stringResource(id = placeholderTextRes), color = Color.Gray) },
        maxLines = 2,
        trailingIcon = {
            if (value.isNotBlank()){
                Icon(Icons.Filled.Done,
                    contentDescription = "Done",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(RoundedCornerShape(dimensionResource(id = roundedCornerRes)))
                        .clickable { onDone.invoke() })
            }
        },
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = true,
            keyboardType = KeyboardType.Text,
            imeAction = if (value.isNotBlank()) ImeAction.Done else ImeAction.None
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onDone.invoke()
            }
        ),
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
            onDone = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}