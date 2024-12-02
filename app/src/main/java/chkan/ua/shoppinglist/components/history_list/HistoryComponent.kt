package chkan.ua.shoppinglist.components.history_list

import chkan.ua.domain.usecases.history.GetHistoryFlowUseCase
import chkan.ua.shoppinglist.core.components.StateComponent
import javax.inject.Inject


class HistoryComponent @Inject constructor(
    getHistoryFlow: GetHistoryFlowUseCase
) : StateComponent<HistoryComponentState>(HistoryComponentState()) {

    init {
        getHistoryFlow.run(Unit).updateStateOnEach { copy(list = it) }
    }

}