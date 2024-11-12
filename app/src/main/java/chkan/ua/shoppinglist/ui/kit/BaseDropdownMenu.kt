package chkan.ua.shoppinglist.ui.kit

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import chkan.ua.shoppinglist.core.models.MenuItem

@Composable
fun BaseDropdownMenu(
    isMenuExpanded: Boolean,
    onDismissRequest: () -> Unit,
    listItems: List<MenuItem>
) {
    DropdownMenu(
        expanded = isMenuExpanded,
        onDismissRequest = onDismissRequest
    ) {
        listItems.forEach { item ->
            DropdownMenuItem(
                text = { Text(text = item.title) },
                onClick = {
                    item.onClick()
                    onDismissRequest.invoke()
                }
            )
        }
    }
}