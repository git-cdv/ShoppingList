package chkan.ua.data.sources.firestore

import android.util.Log
import chkan.ua.core.exceptions.ResourceCode
import chkan.ua.core.exceptions.UserMessageException
import chkan.ua.data.models.RemoteItem
import chkan.ua.data.sources.RemoteDataSource
import chkan.ua.domain.Logger
import chkan.ua.domain.models.Item
import chkan.ua.domain.models.ListItems
import chkan.ua.domain.models.ListSummary
import chkan.ua.domain.objects.Editable
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID


const val collectionPath = "shopping_lists"

@Singleton
class FirestoreSourceImpl @Inject constructor (
    private val firestore: FirebaseFirestore,
    private val logger: Logger
) : RemoteDataSource {

    override suspend fun createSharedList(
        userId: String,
        list: ListItems
    ): String {
        val docRef = firestore.collection(collectionPath).document()
        val remoteModel = list.toRemoteModel(userId,docRef.id)
        docRef.set(remoteModel).await()
        return docRef.id
    }

    override fun getAllListsSummaryFlow(userId: String): Flow<List<ListSummary>> {
        return callbackFlow {
            val listenerRegistration = firestore
                .collection(collectionPath)
                .whereArrayContains("membersIds", userId)
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        logger.e(error, "getAllListsSummaryFlow error: $error")
                        close(error)
                        return@addSnapshotListener
                    }

                    if (querySnapshot != null) {
                        try {
                            val listsSummary = querySnapshot.documents.mapNotNull { document ->
                                val id = document.getString("id") ?: return@mapNotNull null
                                val title = document.getString("title") ?: return@mapNotNull null
                                val createdBy = document.getString("createdBy") ?: return@mapNotNull null

                                // Теперь просто читаем счетчики, не загружая items!
                                val totalItems = document.getLong("totalItems")?.toInt() ?: 0
                                val readyItems = document.getLong("readyItems")?.toInt() ?: 0

                                ListSummary(
                                    id = id,
                                    title = title,
                                    totalItems = totalItems,
                                    readyItems = readyItems,
                                    isOwner = userId == createdBy
                                )
                            }

                            trySend(listsSummary)
                        } catch (e: Exception) {
                            logger.e(e, "getAllListsSummaryFlow parsing error: $e")
                            close(e)
                        }
                    } else {
                        trySend(emptyList())
                    }
                }

            awaitClose { listenerRegistration.remove() }
        }.flowOn(Dispatchers.IO)
    }

    override fun getListWithItemsFlowById(listId: String): Flow<List<Item>> {
        return callbackFlow {
            val listenerRegistration = firestore
                .collection(collectionPath)
                .document(listId)
                .addSnapshotListener { documentSnapshot, error ->
                    if (error != null) {
                        logger.e(error,"getListWithItemsFlowById e:$error")
                        close(error)
                        return@addSnapshotListener
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        try {
                            val items = documentSnapshot.get("items") as? List<Map<String, Any>>
                            val itemsList = items?.map { itemMap ->
                                Item(
                                    itemId = itemMap["itemId"] as? String ?: "",
                                    content = itemMap["content"] as? String ?: "",
                                    listId = itemMap["listId"] as? String ?: "",
                                    isReady = itemMap["ready"] as? Boolean ?: false,
                                    note = itemMap["note"] as? String,
                                )
                            } ?: emptyList()

                            trySend(itemsList)
                        } catch (e: Exception) {
                            logger.e(e,"getListWithItemsFlowById e:$e")
                            close(e)
                        }
                    } else {
                        trySend(emptyList())
                    }
                }

            awaitClose { listenerRegistration.remove() }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun markItemReady(listId: String, itemId: String, isReady: Boolean) {
        val docRef = firestore.collection(collectionPath).document(listId)

        firestore.runTransaction { transaction ->
            val document = transaction.get(docRef)
            val items = document.get("items") as? List<Map<String, Any>> ?: emptyList()

            // Находим item и проверяем текущее состояние
            val currentItem = items.find { it["itemId"] == itemId }
            val currentIsReady = currentItem?.get("ready") as? Boolean ?: false

            // Обновляем только если состояние действительно изменилось
            if (currentIsReady != isReady) {
                // Обновляем item в массиве
                val updatedItems = items.map { item ->
                    if (item["itemId"] == itemId) {
                        item.toMutableMap().apply {
                            put("ready", isReady)
                        }
                    } else item
                }

                // Обновляем счетчик
                val increment = if (isReady) 1L else -1L

                transaction.update(docRef, mapOf(
                    "items" to updatedItems,
                    "readyItems" to FieldValue.increment(increment)
                ))
            }
        }.await()
    }

    override suspend fun addItem(item: Item) {
        val docRef = firestore.collection(collectionPath).document(item.listId)

        val itemMap = mapOf(
            "itemId" to item.itemId,
            "content" to item.content,
            "listId" to item.listId,
            "ready" to item.isReady,
            "note" to item.note
        )

        val updates = mutableMapOf<String, Any>(
            "items" to FieldValue.arrayUnion(itemMap),
            "totalItems" to FieldValue.increment(1)
        )

        // Если item добавляется как готовый, увеличиваем счетчик
        if (item.isReady) {
            updates["readyItems"] = FieldValue.increment(1)
        }

        docRef.update(updates).await()
    }

    override suspend fun deleteItem(listId: String, itemId: String, wasReady: Boolean) {
        val docRef = firestore.collection(collectionPath).document(listId)

        firestore.runTransaction { transaction ->
            val document = transaction.get(docRef)
            val items = document.get("items") as? List<Map<String, Any>> ?: emptyList()

            // Удаляем item из массива
            val updatedItems = items.filter { it["itemId"] != itemId }

            val updates = mutableMapOf<String, Any>(
                "items" to updatedItems,
                "totalItems" to FieldValue.increment(-1)
            )

            // Если удаленный item был готов, уменьшаем счетчик
            if (wasReady) {
                updates["readyItems"] = FieldValue.increment(-1)
            }

            transaction.update(docRef, updates)
        }.await()
    }

    override suspend fun clearReadyItems(listId: String) {
        val docRef = firestore.collection(collectionPath).document(listId)

        firestore.runTransaction { transaction ->
            val document = transaction.get(docRef)
            val items = document.get("items") as? List<Map<String, Any>> ?: emptyList()
            
            val notReadyItems = items.filter { !(it["ready"] as? Boolean ?: false) }
            val readyItemsCount = items.size - notReadyItems.size

            transaction.update(docRef, mapOf(
                "items" to notReadyItems,
                "totalItems" to FieldValue.increment(-readyItemsCount.toLong()),
                "readyItems" to 0
            ))
        }.await()
    }

    override suspend fun editItem(listId: String, editable: Editable) {
        val docRef = firestore.collection(collectionPath).document(listId)

        firestore.runTransaction { transaction ->
            val document = transaction.get(docRef)
            val items = document.get("items") as? List<Map<String, Any>> ?: emptyList()

            val updatedItems = items.map { existingItem ->
                if (existingItem["itemId"] == editable.id) {
                    existingItem.toMutableMap().apply {
                        put("content", editable.title)
                        editable.note?.let { put("note", it) }
                    }
                } else existingItem
            }

            transaction.update(docRef, "items", updatedItems)
        }.await()
    }

    override suspend fun editList(config: Editable) {
        val docRef = firestore.collection(collectionPath).document(config.id)
        docRef.update("title", config.title).await()
    }

    override suspend fun deleteList(listId: String){
        val docRef = firestore.collection(collectionPath).document(listId)
        docRef.delete().await()
    }

    override suspend fun getListWithItemsById(listId: String): ListItems {
        val docRef = firestore.collection(collectionPath).document(listId)
        val document = docRef.get().await()

        if (!document.exists()) throw Exception("Document not found")

        val newListId = UUID.randomUUID().toString().take(6)
        val items = (document.get("items") as? List<Map<String, Any>>)?.map { itemMap ->
            Item(
                itemId = itemMap["itemId"] as? String ?: "",
                content = itemMap["content"] as? String ?: "",
                listId = newListId,
                isReady = itemMap["ready"] as? Boolean ?: false,
                note = itemMap["note"] as? String,
            )
        } ?: emptyList()

        return ListItems(
            id = newListId,
            title = document.getString("title") ?: "",
            items = items,
            position = 0,
            isShared = false
        )
    }

    override suspend fun joinSharedList(userId: String, listId: String) {
        withContext(Dispatchers.IO) {
            try {
                val docRef = firestore.collection(collectionPath).document(listId)

                val document = docRef.get().await()
                if (!document.exists()) {
                    throw UserMessageException(ResourceCode.JOINING_LIST_NOT_FOUND)
                }

                val currentMembers = document.get("membersIds") as? List<String> ?: emptyList()
                if (currentMembers.contains(userId)) {
                    throw UserMessageException(ResourceCode.JOINING_USER_ALREADY_MEMBER)
                }

                docRef.update("membersIds", FieldValue.arrayUnion(userId)).await()
            } catch (e: Exception) {
                if (e is UserMessageException){
                    throw e
                }else {
                    if (e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED){
                        throw UserMessageException(ResourceCode.JOINING_LIST_NOT_FOUND)
                    } else {
                        throw UserMessageException(ResourceCode.UNKNOWN_ERROR)
                    }
                }
            }
        }
    }

    private fun ListItems.toRemoteModel(createdBy: String, docRefId: String): HashMap<String, Any> {
        val readyCount = this.items.count { it.isReady }
        return hashMapOf(
            "id" to docRefId,
            "title" to this.title,
            "items" to this.items
                .sortedBy { it.position }
                .map { item -> RemoteItem(
                    itemId = item.itemId,
                    content = item.content,
                    listId = docRefId,
                    isReady = item.isReady,
                    note = item.note
                ) },
            "createdBy" to createdBy,
            "createdAt" to FieldValue.serverTimestamp(),
            "membersIds" to listOf(createdBy),
            "totalItems" to this.items.size,
            "readyItems" to readyCount
        )
    }


}