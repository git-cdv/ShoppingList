package chkan.ua.data.sources.firestore

import chkan.ua.data.sources.RemoteDataSource
import chkan.ua.domain.Logger
import chkan.ua.domain.models.Item
import chkan.ua.domain.models.ListItems
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers


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
                                    position = itemMap["position"] as? Int ?: 0,
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


    private fun ListItems.toRemoteModel(createdBy: String, docRefId: String): HashMap<String, Any> {
        return hashMapOf(
            "id" to docRefId,
            "title" to this.title,
            "position" to this.position,
            "items" to this.items,
            "createdBy" to createdBy,
            "createdAt" to FieldValue.serverTimestamp(),
            "membersIds" to listOf(createdBy)
        )
    }


}