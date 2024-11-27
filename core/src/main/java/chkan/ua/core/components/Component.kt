package chkan.ua.core.components

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

interface Component : ComponentStoreOwner {

    val coroutineScope: CoroutineScope

    fun clear() {
        coroutineScope.cancel()
        clearComponents()
    }
}