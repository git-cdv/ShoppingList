package chkan.ua.shoppinglist.ui.kit.bottom_sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.ui.kit.buttons.MyButton
import chkan.ua.shoppinglist.ui.kit.buttons.MyOutlinedButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteJoinBottomSheet(
    sheetState: SheetState,
    onJoin: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
){
    ModalBottomSheet(
        onDismissRequest = { onDismiss.invoke()},
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.root_padding)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.ready_join),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = dimensionResource(id = R.dimen.root_padding))
                    .fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = R.dimen.inner_padding)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.inner_padding))
            ) {
                MyButton(stringResource(id = R.string.join), Modifier.weight(1f)) { onJoin() }
                MyOutlinedButton(stringResource(id = R.string.cancel), Modifier.weight(1f)) { onDismiss() }
            }
        }
    }
}