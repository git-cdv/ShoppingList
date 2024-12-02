package chkan.ua.shoppinglist.components.history_list

import chkan.ua.shoppinglist.core.components.StateDelegate

//need for preview with main screen
interface PreviewStubComponent : StateDelegate<HistoryComponentState> {
    fun onPreview()
}