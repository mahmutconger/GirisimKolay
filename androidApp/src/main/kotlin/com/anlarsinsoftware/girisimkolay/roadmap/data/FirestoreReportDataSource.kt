package com.anlarsinsoftware.girisimkolay.roadmap.data

import com.anlarsinsoftware.girisimkolay.roadmap.data.dto.RoadmapReportDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreReportDataSource(
    private val firestore: FirebaseFirestore
) {
    suspend fun loadReport(uid: String, reportId: String): RoadmapReportDto? {
        val snapshot = firestore.collection("users")
            .document(uid)
            .collection("reports")
            .document(reportId)
            .get()
            .await()
        if (!snapshot.exists()) return null
        return (snapshot.data ?: emptyMap<String, Any?>()).toRoadmapReportDto()
    }
}
