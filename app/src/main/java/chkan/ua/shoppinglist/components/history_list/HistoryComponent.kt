package chkan.ua.shoppinglist.components.history_list

import chkan.ua.domain.usecases.history.GetHistoryFlowUseCase
import chkan.ua.shoppinglist.core.components.StateComponent
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.StubHistoryComponent
import javax.inject.Inject


class HistoryComponent @Inject constructor(
    private val getHistoryFlow: GetHistoryFlowUseCase
) : StateComponent<HistoryComponentState>(HistoryComponentState()), StubHistoryComponent {

    fun initFlow(listId: String){
        getHistoryFlow(listId).updateStateOnEach { copy(list = it) }
    }

}