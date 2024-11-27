package chkan.ua.core.components

interface ComponentStoreOwner {

    val componentStore: ComponentStore

    fun attachComponent(component: Component) {
        componentStore.put(component)
    }

    fun detachComponent(component: Component) {
        componentStore.remove(component)
    }

    fun clearComponents() {
        componentStore.clear()
    }
}