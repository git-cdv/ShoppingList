package chkan.ua.shoppinglist.utils

import java.util.Locale

fun Locale.isCurrentLocale(supportedLocaleCodes: List<String>): Boolean {

    val lowerCaseList = supportedLocaleCodes.map { it.lowercase() }
    val languageOnly = this.language.lowercase()

    return when {
        lowerCaseList.contains(languageOnly) -> true
        else -> false
    }
}