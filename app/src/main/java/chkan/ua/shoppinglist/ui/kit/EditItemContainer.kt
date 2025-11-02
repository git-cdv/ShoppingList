package chkan.ua.shoppinglist.ui.kit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
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

@Composable
fun EditItemContainer(
    itemText: TextFieldValue,
    noteText: String,
    onItemChange: (TextFieldValue) -> Unit,
    onNoteChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onDone: () -> Unit,
    modifier: Modifier,
    maxLength: Int? = null
) {
    Column(modifier = modifier) {
        BasicTextField(
            value = itemText,
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

                onItemChange(
                    TextFieldValue(
                        text = limitedText,
                        selection = newSelection
                    )
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onDone.invoke()
                }
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            maxLines = 4,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            decorationBox = { innerTextField ->
                Box{
                    if (itemText.text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.items_text_placeholder),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    innerTextField()
                }
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp,bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = noteText,
                onValueChange = { newText ->
                    val limitedText = if (maxLength != null && newText.length > maxLength) {
                        newText.take(maxLength)
                    } else {
                        newText
                    }
                    onNoteChange(limitedText)
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                ),
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onDone.invoke()
                    }
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                maxLines = 4,
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    Box{
                        if (noteText.isEmpty()) {
                            Text(
                                text = stringResource(R.string.add_note),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        innerTextField()
                    }
                }
            )

            if (itemText.text.isNotBlank()){
                Icon(
                    Icons.Filled.Done,
                    contentDescription = "Done",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(28.dp)
                        .clip(RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)))
                        .clickable { onDone.invoke() })
            } else {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditItemContainerPreview() {
    ShoppingListTheme {
        EditItemContainer(
            itemText = TextFieldValue(""),
            noteText = "",
            onItemChange = {},
            onNoteChange = {},
            onDone = {},
            focusRequester = FocusRequester(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}