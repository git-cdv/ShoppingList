package chkan.ua.core.services

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateProvider {
    /**
     * Returns the current date as a formatted string based on the specified pattern.
     *
     * @param pattern The format pattern for the date. For example:
     * - `"dd MMMM yyyy"` for output like "05 November 2024".
     * - `"yyyy-MM-dd"` for output like "2024-11-05".
     * - `"EEEE, dd MMMM yyyy"` for output like "Tuesday, 05 November 2024".
     *
     * The pattern may include:
     * - `d` — day of the month (without leading zero if it's a single-digit day).
     * - `dd` — day of the month with leading zero.
     * - `MMM` — abbreviated month name (e.g., "Nov").
     * - `MMMM` — full month name (e.g., "November").
     * - `yyyy` — year.
     * - `EEEE` — day of the week (e.g., "Tuesday").
     *
     * @return A string representing the current date, formatted according to the specified pattern and the device's locale.
     *
     * @throws IllegalArgumentException if the pattern is invalid or unsupported.
     */
    fun getTodayByPattern(pattern: String): String{
        val currentDate = Date()
        try {
            val formatter = SimpleDateFormat(pattern, Locale.getDefault())
            return formatter.format(currentDate)
        } catch (e: Exception){
            val formatter = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            return formatter.format(currentDate)
        }
    }
}