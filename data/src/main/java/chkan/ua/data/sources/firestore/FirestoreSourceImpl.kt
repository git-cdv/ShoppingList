package chkan.ua.data.sources.firestore

import chkan.ua.data.models.ItemEntity
import chkan.ua.data.models.ListEntity
import chkan.ua.data.models.ListWithItems
import chkan.ua.data.sources.DataSource
import chkan.ua.domain.objects.Editable
import chkan.ua.domain.usecases.auth.AuthManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreSourceImpl @Inject constructor (
    private val db: FirebaseFirestore,
    private val authManager: AuthManager
) : DataSource {
    override fun getListsWithItemsFlow(): Flow<List<ListWithItems>> {
        TODO("Not yet implemented")
    }

    override fun getItemsFlowByListId(listId: Int): Flow<List<ItemEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun addList(list: ListEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteList(listId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTitle(editable: Editable) {
        TODO("Not yet implemented")
    }

    override suspend fun getListCount(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getMaxListPosition(): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun getMaxItemPosition(): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun addItem(item: ItemEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItem(itemId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun markItemReady(itemId: Int, state: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun clearReadyItems(listId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun updateContent(editable: Editable) {
        TODO("Not yet implemented")
    }

    override suspend fun moveToTop(id: Int, position: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun moveItemToTop(id: Int, position: Int) {
        TODO("Not yet implemented")
    }
}