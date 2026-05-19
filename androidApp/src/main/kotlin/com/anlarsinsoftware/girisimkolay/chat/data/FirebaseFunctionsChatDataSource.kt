package com.anlarsinsoftware.girisimkolay.chat.data

import android.util.Log
import com.anlarsinsoftware.girisimkolay.chat.data.dto.ChatResponse
import com.anlarsinsoftware.girisimkolay.chat.data.dto.ChatResponseMessageDto
import com.anlarsinsoftware.girisimkolay.chat.data.dto.CitationDto
import com.anlarsinsoftware.girisimkolay.chat.data.dto.ProfilingSnapshotDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await

class FirebaseFunctionsChatDataSource(
    private val functions: FirebaseFunctions,
    private val auth: FirebaseAuth
) {
    suspend fun sendMessage(
        sessionId: String?,
        text: String,
        clientRequestId: String
    ): ChatResponse {
        Log.i(
            "FirebaseFunctionsChat",
            "Calling sendChatMessage. sessionId=${sessionId ?: "new"}, requestId=$clientRequestId"
        )

        // Force token refresh to ensure authentication is sent correctly to the function
        try {
            auth.currentUser?.getIdToken(true)?.await()
            Log.d("FirebaseFunctionsChat", "Auth token refreshed successfully.")
        } catch (e: Exception) {
            Log.w("FirebaseFunctionsChat", "Failed to refresh auth token: ${e.message}")
        }

        val payload = mapOf(
            "sessionId" to sessionId,
            "text" to text,
            "clientRequestId" to clientRequestId
        )
        return try {
            Log.d("FirebaseFunctionsChat", "Attempting HTTPS callable: sendChatMessage")
            val result = functions
                .getHttpsCallable("sendChatMessage")
                .call(payload)
                .await()
            
            val data = result.data as? Map<*, *> ?: error("Malformed chat response.")
            Log.i("FirebaseFunctionsChat", "sendChatMessage call succeeded.")
            data.toChatResponse()
        } catch (exception: Exception) {
            Log.e("FirebaseFunctionsChat", "sendChatMessage call failed. Error: ${exception.message}", exception)
            throw exception
        }
    }
}

private fun Map<*, *>.toChatResponse(): ChatResponse = ChatResponse(
    sessionId = this["sessionId"] as? String ?: error("Missing sessionId."),
    message = (this["message"] as? Map<*, *> ?: error("Missing message.")).toChatMessageDto(),
    answer = this["answer"] as? String ?: "",
    citations = (this["citations"] as? List<*>)?.mapNotNull { (it as? Map<*, *>)?.toCitationDto() }.orEmpty(),
    profileDelta = (this["profileDelta"] as? Map<*, *>)?.toProfileDto(),
    confidence = (this["confidence"] as? Number)?.toDouble() ?: 0.0,
    nextActions = (this["nextActions"] as? List<*>)?.mapNotNull { it as? String }.orEmpty(),
    insufficientEvidence = this["insufficientEvidence"] as? Boolean ?: false
)

private fun Map<*, *>.toChatMessageDto(): ChatResponseMessageDto = ChatResponseMessageDto(
    id = this["id"] as? String ?: error("Missing message id."),
    sessionId = this["sessionId"] as? String ?: error("Missing message sessionId."),
    text = this["text"] as? String ?: "",
    isFromUser = this["isFromUser"] as? Boolean ?: false,
    timestamp = (this["timestamp"] as? Number)?.toLong() ?: 0L,
    citations = (this["citations"] as? List<*>)?.mapNotNull { (it as? Map<*, *>)?.toCitationDto() }.orEmpty(),
    profileDelta = (this["profileDelta"] as? Map<*, *>)?.toProfileDto(),
    confidence = (this["confidence"] as? Number)?.toDouble(),
    nextActions = (this["nextActions"] as? List<*>)?.mapNotNull { it as? String }.orEmpty()
)

private fun Map<*, *>.toCitationDto(): CitationDto = CitationDto(
    sourceName = this["sourceName"] as? String ?: this["source_name"] as? String ?: "Kaynak",
    section = this["section"] as? String,
    snippet = this["snippet"] as? String,
    sourceUrl = this["sourceUrl"] as? String ?: this["source_url"] as? String
)

private fun Map<*, *>.toProfileDto(): ProfilingSnapshotDto = ProfilingSnapshotDto(
    businessIdea = this["businessIdea"] as? String ?: this["business_idea"] as? String,
    businessSector = this["businessSector"] as? String ?: this["business_sector"] as? String,
    preferredCompanyType = this["preferredCompanyType"] as? String ?: this["preferred_company_type"] as? String,
    experienceLevel = this["experienceLevel"] as? String ?: this["experience_level"] as? String,
    fundingNeed = this["fundingNeed"] as? String ?: this["funding_need"] as? String,
    legalConcerns = (this["legalConcerns"] as? List<*>)?.mapNotNull { it as? String }
        ?: (this["legal_concerns"] as? List<*>)?.mapNotNull { it as? String }
        ?: emptyList()
)
