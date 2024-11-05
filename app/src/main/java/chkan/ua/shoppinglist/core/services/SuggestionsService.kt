package chkan.ua.shoppinglist.core.services

import android.content.Context
import chkan.ua.shoppinglist.R

class SuggestionsService {

    fun withToday(today: String, context: Context): List<String> {
        return listOf(
            context.getString(R.string.groceries),
            context.getString(R.string.shopping),
            today,
            context.getString(R.string.weekend),
            context.getString(R.string.urgently),
            context.getString(R.string.food),
            context.getString(R.string.trip),
            context.getString(R.string.home),
            context.getString(R.string.drug),
            context.getString(R.string.supermarket)
        )
    }
}