package chkan.ua.shoppinglist.core.abstracts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chkan.ua.shoppinglist.core.services.LoadingManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val loadingManager = LoadingManager()
    val isLoading: StateFlow<Boolean> = loadingManager.isLoading

    protected suspend fun <T> withLoading(operation: suspend () -> T): T {
        return try {
            loadingManager.startOperation()
            operation()
        } finally {
            loadingManager.finishOperation()
        }
    }

    protected fun launchWithLoading(operation: suspend () -> Unit) {
        viewModelScope.launch {
            withLoading { operation() }
        }
    }
}
