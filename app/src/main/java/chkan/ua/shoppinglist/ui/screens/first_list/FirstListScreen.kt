package chkan.ua.shoppinglist.ui.screens.first_list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import chkan.ua.core.services.DateProvider
import chkan.ua.shoppinglist.R
import chkan.ua.shoppinglist.core.services.SuggestionsProvider
import chkan.ua.shoppinglist.navigation.FirstListRoute
import chkan.ua.shoppinglist.navigation.ItemsRoute
import chkan.ua.shoppinglist.navigation.ListsRoute
import chkan.ua.shoppinglist.navigation.localNavController
import chkan.ua.shoppinglist.ui.kit.RoundedTextField
import chkan.ua.shoppinglist.ui.kit.SuggestionsHorizontalList
import chkan.ua.shoppinglist.ui.screens.lists.ListsViewModel
import chkan.ua.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun FirstListScreen(
    listsViewModel: ListsViewModel = hiltViewModel()
){
    val navController = localNavController.current
    val today = DateProvider().getTodayByPattern("dd.MM.yy")
    val suggestions = SuggestionsProvider().withToday(today,LocalContext.current)

    FirstListContent(suggestions){ title ->
        listsViewModel.addList(title)
        navController.navigate(ListsRoute){
            popUpTo(FirstListRoute) { inclusive = true }
        }
    }
}

@Composable
fun FirstListContent(suggestions: List<String>, addListWithTitle: (String) -> Unit){

    var listNameText by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (textField, textTitle, suggestionsView) = createRefs()
        val centerLine = createGuidelineFromTop(0.5f)

        RoundedTextField(
            value = listNameText,
            onValueChange = {newText -> listNameText = newText},
            roundedCornerRes = R.dimen.rounded_corner,
            placeholderTextRes = R.string.first_list_text_placeholder,
            onDone = { addListWithTitle.invoke(listNameText) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .padding(horizontal = dimensionResource(id = R.dimen.horizontal_root_padding))
                .constrainAs(textField) {
                    bottom.linkTo(centerLine)
                }
        )

        Text(
            text = stringResource(id = R.string.first_list_text_title),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.horizontal_root_padding), vertical = dimensionResource(id = R.dimen.vertical_outer_padding))
                .constrainAs(textTitle) {
                    bottom.linkTo(textField.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })

        SuggestionsHorizontalList(
            suggestions = suggestions,
            onSuggestionChoose = {
                suggestion -> listNameText = suggestion
                focusManager.clearFocus()
                                 },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(id = R.dimen.vertical_inner_padding))
                .constrainAs(suggestionsView) {
                    top.linkTo(textField.bottom)
                }
        )

    }
}

@Preview(showBackground = true)
@Composable
fun FirstListPreview() {
    ShoppingListTheme {
        FirstListContent(listOf("Products", "Today", "05.11.24", "Home")) {}
    }
}