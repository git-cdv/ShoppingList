package chkan.ua.shoppinglist.core.components

import androidx.lifecycle.ViewModel

class StateViewModel <S>(state: S) : ViewModel(), StateComponent<S>(state) {
}