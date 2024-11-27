package chkan.ua.shoppinglist.core.components

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class StateComponent<S>(initialState: S) : BaseComponent(), StateDelegate<S> {

    private val stateDelegate = StateDelegateImpl(initialState)
    override val stateFlow: StateFlow<S>
        get() = stateDelegate.stateFlow

    override fun updateState(reducer: S.() -> S) = stateDelegate.updateState(reducer)

    protected fun <T> Flow<T>.updateStateOnEach(reducer: S.(T) -> S) {
        onEach { updateState { reducer(it) } }.launchIn(coroutineScope)
    }
}