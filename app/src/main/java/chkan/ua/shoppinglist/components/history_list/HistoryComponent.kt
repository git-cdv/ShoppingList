package chkan.ua.shoppinglist.components.history_list

import chkan.ua.domain.usecases.history.GetHistoryFlowUseCase
import chkan.ua.shoppinglist.core.components.StateComponent
import chkan.ua.shoppinglist.ui.kit.bottom_sheets.PreviewStubHistoryComponent
import javax.inject.Inject


class HistoryComponent @Inject constructor(
    getHistoryFlow: GetHistoryFlowUseCase
) : StateComponent<HistoryComponentState>(HistoryComponentState()), PreviewStubHistoryComponent {

    init {
        getHistoryFlow.run(Unit).updateStateOnEach { copy(list = it) }
    }

}