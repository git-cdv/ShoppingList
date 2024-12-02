package chkan.ua.shoppinglist.core.components

import androidx.lifecycle.ViewModel

abstract class ComponentsViewModel(
    override val componentStore: ComponentStore = ComponentStore()
): ViewModel(), ComponentStoreOwner {

    override fun onCleared() {
        clearComponents()
        super.onCleared()
    }

}