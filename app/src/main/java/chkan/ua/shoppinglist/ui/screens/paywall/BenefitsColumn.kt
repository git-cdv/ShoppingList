package chkan.ua.shoppinglist.ui.screens.paywall

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme

data class BenefitsItem(val icon: Int, val text: String)

@Composable
fun BenefitsColumn(items: List<BenefitsItem>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        items.onEachIndexed { index, item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(item.icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 2.dp, end = 8.dp).size(28.dp)
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = item.text,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Start
                )
            }
            if (index != items.lastIndex) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun BenefitsColumnPreview() {
    ShoppingListTheme {
        val items = listOf(
            BenefitsItem(R.drawable.ic_shield, stringResource(R.string.no_annoying_ads)),
            BenefitsItem(R.drawable.ic_shield, stringResource(R.string.no_annoying_ads)),
            BenefitsItem(R.drawable.ic_shield, stringResource(R.string.no_annoying_ads))
        )
        BenefitsColumn(items)
    }
}