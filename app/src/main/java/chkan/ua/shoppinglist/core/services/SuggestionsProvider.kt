package chkan.ua.shoppinglist.core.services

import android.content.Context
import chkan.ua.shoppinglist.R

class SuggestionsProvider {

    fun withToday(today: String, context: Context): List<String> {
        return listOf(
            context.getString(R.string.to_do_list),
            context.getString(R.string.groceries),
            today,
            context.getString(R.string.wishlist),
            context.getString(R.string.ideas),
            context.getString(R.string.reading_list),
            context.getString(R.string.watchlist)
        )
    }
}