package com.anlarsinsoftware.girisimkolay.chat.data

import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatSessionSummary
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirestoreChatSessionsDataSource(
    private val firestore: FirebaseFirestore
) {
    suspend fun loadRecentSessions(uid: String): List<ChatSessionSummary> =
        firestore.collection("users").document(uid)
            .collection("chatSessions")
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                val title = doc.getString("title") ?: return@mapNotNull null
                ChatSessionSummary(
                    id = doc.id,
                    title = title,
                    updatedAt = doc.getLong("updatedAt") ?: 0L
                )
            }
}
