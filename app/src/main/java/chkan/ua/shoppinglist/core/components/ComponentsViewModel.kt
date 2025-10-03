package chkan.ua.shoppinglist.core.components

import chkan.ua.shoppinglist.core.abstracts.BaseViewModel

abstract class ComponentsViewModel(
    override val componentStore: ComponentStore = ComponentStore()
): BaseViewModel(), ComponentStoreOwner {

    override fun onCleared() {
        clearComponents()
        super.onCleared()
    }

}