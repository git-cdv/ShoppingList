package chkan.ua.shoppinglist.ui.kit.buttons

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun SimpleButton(
    text: String,
    onClicked: ()->Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    isEnable: Boolean = true,
    textSize: Int? = null,
    heightSize: Int? = null
) {
    val buttonHeight = heightSize?.dp ?: dimensionResource(id = R.dimen.button_height)
    val cornerRadius = dimensionResource(id = R.dimen.button_corner_radius)

    Button(
        onClick = { onClicked.invoke()},
        enabled = isEnable,
        modifier = modifier
            .fillMaxWidth()
            .height(buttonHeight)
            .clip(RoundedCornerShape(cornerRadius)),
        shape = RoundedCornerShape(cornerRadius),
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = if (textSize == null) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelLarge.copy(fontSize = textSize.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.then(if (isEnable) Modifier else Modifier.alpha(0.38f))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SimpleButtonPreview() {
    ShoppingListTheme {
        SimpleButton(
            text = "Continue",
            onClicked = {},
            modifier = Modifier,
            isEnable = false
        )
    }
}