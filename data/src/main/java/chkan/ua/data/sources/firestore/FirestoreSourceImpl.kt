package chkan.ua.data.sources.firestore

import chkan.ua.data.sources.RemoteDataSource
import chkan.ua.domain.models.ListItems
import chkan.ua.domain.usecases.auth.AuthManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

const val collectionPath = "shopping_lists"

@Singleton
class FirestoreSourceImpl @Inject constructor (
    private val firestore: FirebaseFirestore,
    private val authManager: AuthManager
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