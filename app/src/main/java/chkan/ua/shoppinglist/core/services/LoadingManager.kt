package chkan.ua.shoppinglist.core.services

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicInteger

class LoadingManager {
    private val activeOperationsCount = AtomicInteger(0)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun startOperation() {
        val count = activeOperationsCount.incrementAndGet()
        if (count == 1) {
            _isLoading.value = true
        }
    }

    fun finishOperation() {
        val count = activeOperationsCount.decrementAndGet()
        if (count <= 0) {
            activeOperationsCount.set(0)
            _isLoading.value = false
        }
    }
}
