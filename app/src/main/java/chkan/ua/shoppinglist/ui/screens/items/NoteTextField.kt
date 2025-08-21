package chkan.ua.shoppinglist.ui.screens.items

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun NoteTextField(
    text: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    borderColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
    textColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
    maxLines: Int = 2,
    maxLength: Int = 100,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
    ),
    keyboardActions: KeyboardActions = KeyboardActions(),
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor),
    onClose: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)

    BasicTextField(
        value = text,
        onValueChange = { newText ->
            val limitedText = if (newText.length > maxLength) {
                newText.take(maxLength)
            } else {
                newText
            }
            onValueChange(limitedText)
        },
        modifier = modifier
            .then(
                if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier
            ),
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        singleLine = maxLines == 1,
        maxLines = maxLines,
        keyboardActions = keyboardActions,
        cursorBrush = SolidColor(textColor),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .clip(shape)
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = shape
                    )
                    .padding(
                        horizontal = 16.dp,
                        vertical = if (maxLines == 1) 18.dp else 14.dp
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        innerTextField()
                    }

                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = borderColor,
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .clickable {
                                onClose.invoke()
                            }
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun NoteTextFieldPreview() {
    ShoppingListTheme {
        Column {
            NoteTextField(
                text = "Пример текста",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                onClose = {}
            )
        }
    }
}
