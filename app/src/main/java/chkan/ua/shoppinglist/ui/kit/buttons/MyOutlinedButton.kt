package chkan.ua.shoppinglist.ui.kit.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import chkan.ua.shoppinglist.R

@Composable
fun MyOutlinedButton(text: String, modifier: Modifier, onClick: ()->Unit){
    OutlinedButton(
        onClick = { onClick.invoke() },
        border = BorderStroke(dimensionResource(id = R.dimen.normal_border_width), MaterialTheme.colorScheme.primary),
        modifier = modifier
    ) {
        Text(text = text,style = MaterialTheme.typography.titleMedium)
    }
}