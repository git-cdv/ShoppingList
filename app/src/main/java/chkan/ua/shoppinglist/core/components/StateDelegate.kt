package chkan.ua.shoppinglist.core.components

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface StateDelegate<S> {

    val stateFlow: StateFlow<S>
    val state: S
        get() = stateFlow.value

    fun updateState(reducer: S.() -> S)
}

open class StateDelegateImpl<S>(initialState: S) : StateDelegate<S> {

    private val _stateFlow = MutableStateFlow(initialState)
    override val stateFlow: StateFlow<S> = _stateFlow.asStateFlow()

    override fun updateState(reducer: S.() -> S) = _stateFlow.update(reducer)
}