package chkan.ua.shoppinglist.ui.screens.paywall

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun PaywallItemCardInReview(item: PaywallItem, onItemSelected: (PaywallItem) -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(0.dp),
        onClick = { onItemSelected.invoke(item) },
        modifier = Modifier
            .width(102.dp)
            .height(139.dp)
            .then(
                if (item.isSelected) {
                    Modifier.shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                } else {
                    Modifier
                }
            )
            .padding(4.dp),
        border = BorderStroke(width = 1.dp, color = if (item.isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.secondaryContainer),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (item.type != PaywallType.WEEK){
                val backgroundModifier  = if (item.isSelected){
                    Modifier.background(brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFC6CFFF),
                            Color(0xFF506DFF),
                            Color(0xFF3A57E8)
                        ),
                        center = Offset(0f, 0f),
                        radius = 200f
                    ))
                } else {
                    Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp)
                        .then(backgroundModifier)
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (item.type == PaywallType.YEAR) stringResource(R.string.best_value) else stringResource(R.string.popular),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (item.isSelected) Color.White else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(26.dp))
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.topName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (item.isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = item.botName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (item.isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                )
            }
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            Box(
                modifier = Modifier
                    .fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.price,
                    style = MaterialTheme.typography.labelSmall,
                    overflow = TextOverflow.Ellipsis,
                    color = if (item.isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaywallItemV2CardPreview() {
    ShoppingListTheme {
        PaywallItemCardInReview(
            PaywallItem(
                id = "1",
                type = PaywallType.WEEK,
                isSelected = true,
                price = "$59.99",
                onlyPrice = "only $1.16/w",
                topName = "Monthly",
                botName = "Plan"
            ), {}
        )
    }
}