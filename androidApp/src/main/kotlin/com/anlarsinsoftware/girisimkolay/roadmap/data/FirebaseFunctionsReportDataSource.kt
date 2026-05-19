package com.anlarsinsoftware.girisimkolay.roadmap.data

import android.util.Log
import com.anlarsinsoftware.girisimkolay.roadmap.data.dto.RoadmapReportDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await

class FirebaseFunctionsReportDataSource(
    private val functions: FirebaseFunctions,
    private val auth: FirebaseAuth
) {
    suspend fun generateReport(sessionId: String): RoadmapReportDto {
        // Force token refresh to ensure authentication is sent correctly to the function
        try {
            auth.currentUser?.getIdToken(true)?.await()
            Log.d("FirebaseFunctionsReport", "Auth token refreshed successfully.")
        } catch (e: Exception) {
            Log.w("FirebaseFunctionsReport", "Failed to refresh auth token: ${e.message}")
        }

        val response = functions
            .getHttpsCallable("generateRoadmapReport")
            .call(mapOf("sessionId" to sessionId))
            .await()
            .data as? Map<*, *> ?: error("Malformed report response.")
        return response.toRoadmapReportDto()
    }
}

internal fun Map<*, *>.toRoadmapReportDto(): RoadmapReportDto = RoadmapReportDto(
    id = this["id"] as? String ?: error("Missing report id."),
    userId = this["userId"] as? String ?: this["user_id"] as? String ?: "",
    sessionId = this["sessionId"] as? String ?: this["session_id"] as? String ?: "",
    title = this["title"] as? String ?: "",
    summary = this["summary"] as? String ?: "",
    fileUrl = this["downloadUrl"] as? String ?: this["fileUrl"] as? String ?: this["file_url"] as? String ?: "",
    generatedAt = (this["generatedAt"] as? Number)?.toLong()
        ?: (this["generated_at"] as? Number)?.toLong()
        ?: 0L,
    approvalStatus = this["approvalStatus"] as? String ?: this["approval_status"] as? String ?: "IDLE",
    nextActions = (this["nextActions"] as? List<*>)?.mapNotNull { it as? String }
        ?: (this["next_actions"] as? List<*>)?.mapNotNull { it as? String }
        ?: emptyList()
)
