package chkan.ua.shoppinglist.core.services

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface SharedPreferencesService {
    fun <T> get(key: String, type: Class<T>): T?
    fun <T> set(key: String, value: T)
}

class SharedPreferencesServiceImpl @Inject constructor (@ApplicationContext context: Context) : SharedPreferencesService {

    companion object {
        const val LAST_OPEN_LIST_ID_INT = "last_list_id"
        const val LAST_OPEN_LIST_TITLE_STR = "last_list_title"
        const val LAST_OPEN_LIST_IS_SHARED = "last_list_shared"
        const val IS_FIRST_LAUNCH = "is_first_launch"
    }

    private val sp: SharedPreferences =
        context.getSharedPreferences("BasePrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sp.edit()

    override fun <T> get(key: String, type: Class<T>): T? {

        if (!sp.contains(key)) {
            return null
        }

        return when (type) {
            String::class.java -> sp.getString(key, null) as T?
            Int::class.java -> sp.getInt(key, 0) as T?
            Boolean::class.java -> sp.getBoolean(key, false) as T?
            Float::class.java -> sp.getFloat(key, 0f) as T?
            Long::class.java -> sp.getLong(key, 0L) as T?
            Set::class.java -> sp.getStringSet(key, emptySet()) as T?
            else -> throw IllegalArgumentException("Unsupported type: $type")
        }
    }

    override fun <T> set(key: String, value: T) {
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
            is Set<*> -> editor.putStringSet(key, value as Set<String>)
            else -> throw IllegalArgumentException("Unsupported value type: ${value!!::class.java.name}")
        }
        editor.apply()
    }
}