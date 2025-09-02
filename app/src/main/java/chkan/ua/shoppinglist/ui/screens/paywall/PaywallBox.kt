package chkan.ua.shoppinglist.ui.screens.paywall

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme
import timber.log.Timber

@Composable
fun PaywallBox(
    paywallUiState: PaywallUiState,
    snackbarHostState: SnackbarHostState,
    list: List<PaywallItem>,
    onItemSelected: (String) -> Unit,
    onSubscribe: () -> Unit,
    onSubscribeRestore: () -> Unit,
    onClose: () -> Unit,
    isInner: Boolean = false,
    modifier: Modifier
) {

    val context = LocalContext.current
    var textButton by remember { mutableStateOf(context.getString(R.string.continue_text)) }
    val isReview = remember { paywallUiState.isReview }
    val isHardPaywall = remember { paywallUiState.isHardPaywall }

    LaunchedEffect(Unit) {
        if (isReview) {
            textButton = collectButtonText(list.first { it.isSelected }, context)
        }
    }

    val transition = remember { Animatable(600f) } // Initial position (outside the screen)

    // Launch appearance animation
    if (!isInner) {
        LaunchedEffect(Unit) {
            transition.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .then(
                if (!isInner)
                    Modifier
                        .graphicsLayer(translationY = transition.value)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                else
                    Modifier.background(color = MaterialTheme.colorScheme.surface)
            )

    ) {
        if (!isHardPaywall && isInner) {
            Button(
                onClick = { onClose.invoke() },
                modifier = Modifier
                    .padding(end = 16.dp, top = 10.dp)
                    .align(Alignment.TopEnd)
                    .size(28.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Center)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.unlimited_access),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(16.dp))
            //MovingCardsRowRTL(MaterialTheme.colorScheme.onSecondaryContainer)
            //MovingCardsRowLTR(MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(list) { item ->
                    if (isReview) {
                        PaywallItemCardInReview(item, onItemSelected = { selectedItem ->
                            onItemSelected.invoke(selectedItem.id)
                            if (isReview) {
                                textButton = collectButtonText(selectedItem, context)
                            }
                        })
                    } else {
                        PaywallItemCardAfterReview(item, onItemSelected = { selectedItem ->
                            onItemSelected.invoke(selectedItem.id)
                        })
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            if (isReview) {
                Text(
                    text = stringResource(R.string.subscription_is_required),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            SimpleButton(
                text = textButton,
                onClicked = { onSubscribe.invoke() },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            if (isReview) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_shield),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.auto_renewable_cancel_anytime),
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(if (isReview) 12.dp else 18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.terms_of_use),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.clickable {
                        goToWebUrl(
                            context,
                            "https://"
                        )
                    })
                Text(
                    text = stringResource(R.string.restore),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.clickable { onSubscribeRestore.invoke() }
                )
                Text(
                    text = stringResource(R.string.privacy_policy),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.clickable {
                        goToWebUrl(
                            context,
                            "https://"
                        )
                    })
            }
            SnackbarHost(snackbarHostState)
            Spacer(
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(20.dp)
            )
        }
    }
}

@Composable
fun SimpleButton(text: String, onClicked: () -> Unit, modifier: Modifier) {
    TODO("Not yet implemented")
}

fun collectButtonText(item: PaywallItem, context: Context): String {
    val period = when (item.type) {
        PaywallType.WEEK -> context.getString(R.string.week_small)
        PaywallType.MONTH -> context.getString(R.string.month_small)
        PaywallType.YEAR -> context.getString(R.string.year_small)
    }
    return "${context.getString(R.string.subscribe_by)} ${item.price}/$period"
}

fun goToWebUrl(context: Context, site: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(site))
        context.startActivity(intent)
    } catch (e: Exception) {
        Timber.e(e)
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun PayWallBoxPreview() {
    ShoppingListTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
        ) {
            val list = listOf(
                PaywallItem(
                    id = "1",
                    type = PaywallType.WEEK,
                    isSelected = false,
                    price = "$59.99",
                    onlyPrice = "only $1.16/w",
                    topName = "1",
                    botName = "botName"
                ), PaywallItem(
                    id = "1",
                    type = PaywallType.MONTH,
                    isSelected = false,
                    price = "$59.99",
                    onlyPrice = "only $1.16/w",
                    topName = "topName",
                    botName = "botName"
                ),
                PaywallItem(
                    id = "1",
                    type = PaywallType.YEAR,
                    isSelected = true,
                    price = "$59.99",
                    onlyPrice = "only $1.16/w",
                    topName = "topName",
                    botName = "botName"
                )
            )
            PaywallBox(
                PaywallUiState(),
                snackbarHostState = SnackbarHostState(),
                list,
                {},
                {},
                {},
                {},
                isInner = false,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}