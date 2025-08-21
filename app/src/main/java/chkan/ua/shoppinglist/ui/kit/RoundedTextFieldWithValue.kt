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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import kotlin.text.take

@Composable
fun RoundedTextFieldWithValue(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    roundedCornerRes: Int,
    placeholderTextRes: Int? = null,
    focusRequester: FocusRequester? = null,
    onDone: () -> Unit,
    modifier: Modifier,
    maxLength: Int? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newText ->
            val limitedText = if (maxLength != null && newText.text.length > maxLength) {
                newText.text.take(maxLength)
            } else {
                newText.text
            }

            val newSelection = if (maxLength != null && newText.text.length > maxLength) {
                TextRange(minOf(newText.selection.start, limitedText.length))
            } else {
                newText.selection
            }

            onValueChange(
                TextFieldValue(
                    text = limitedText,
                    selection = newSelection
                )
            )
        },
        shape = RoundedCornerShape(dimensionResource(id = roundedCornerRes)),
        label = null,
        placeholder = placeholderTextRes?.let{ {Text(stringResource(id = placeholderTextRes), color = Color.Gray)} },
        maxLines = 2,
        trailingIcon = {
            if (value.text.isNotBlank()){
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
            imeAction = if (value.text.isNotBlank()) ImeAction.Done else ImeAction.None
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onDone.invoke()
            }
        ),
        modifier = modifier
            .then(
                if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier
            )

    )
}

@Preview(showBackground = true)
@Composable
fun RoundedTextFieldWithValuePreview() {
    ShoppingListTheme {
        RoundedTextFieldWithValue(
            value = TextFieldValue(""),
            onValueChange ={},
            roundedCornerRes = R.dimen.rounded_corner,
            placeholderTextRes = R.string.first_list_text_placeholder,
            focusRequester = null,
            onDone = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
