package com.anlarsinsoftware.girisimkolay.chat.data

import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatMessage
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.ChatMode
import com.anlarsinsoftware.girisimkolay.chat.domain.entity.Citation
import com.anlarsinsoftware.girisimkolay.profile.domain.entity.ProfilingSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreChatHistoryDataSource(
    private val firestore: FirebaseFirestore
) {
    suspend fun loadMessages(uid: String, sessionId: String): List<ChatMessage> {
        return firestore.collection("users")
            .document(uid)
            .collection("chatSessions")
            .document(sessionId)
            .collection("messages")
            .orderBy("timestamp")
            .get()
            .await()
            .documents
            .map { document ->
                ChatMessage(
                    id = document.getString("id").orEmpty(),
                    sessionId = document.getString("sessionId").orEmpty(),
                    text = document.getString("text").orEmpty(),
                    isFromUser = document.getBoolean("isFromUser") ?: false,
                    timestamp = document.getLong("timestamp") ?: 0L,
                    citations = (document.get("citations") as? List<Map<String, Any?>>)
                        ?.map {
                            Citation(
                                sourceName = it["sourceName"] as? String ?: "Kaynak",
                                section = it["section"] as? String,
                                snippet = it["snippet"] as? String,
                                sourceUrl = it["sourceUrl"] as? String
                            )
                        }
                        .orEmpty(),
                    profileDelta = (document.get("profileDelta") as? Map<String, Any?>)?.toProfilingSnapshot(),
                    confidence = document.getDouble("confidence"),
                    nextActions = (document.get("nextActions") as? List<*>)?.mapNotNull { it as? String }.orEmpty(),
                    mode = ChatMode.fromWireValue(document.getString("mode"))
                )
            }
    }
}

private fun Map<String, Any?>.toProfilingSnapshot(): ProfilingSnapshot = ProfilingSnapshot(
    businessIdea = this["businessIdea"] as? String,
    businessSector = this["businessSector"] as? String,
    preferredCompanyType = this["preferredCompanyType"] as? String,
    experienceLevel = this["experienceLevel"] as? String,
    fundingNeed = this["fundingNeed"] as? String,
    legalConcerns = (this["legalConcerns"] as? List<*>)?.mapNotNull { it as? String }.orEmpty()
)
