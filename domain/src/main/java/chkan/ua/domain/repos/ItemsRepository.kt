package chkan.ua.domain.repos

import chkan.ua.domain.models.Item
import kotlinx.coroutines.flow.Flow

interface ItemsRepository {
    fun getListWithItemsFlowById(listId: Int): Flow<List<Item>>
    suspend fun addItem(config: Item)
}