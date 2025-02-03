package chkan.ua.shoppinglist.ui.kit.bottom_sheets

sealed class BottomSheetAction {
    data class SetIsOpen(val isOpen: Boolean) : BottomSheetAction()
    data class SetText(val text: String) : BottomSheetAction()
}