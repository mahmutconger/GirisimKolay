package com.anlarsinsoftware.girisimkolay.roadmap.data.source

/**
 * Platform-specific storage for roadmap related data (e.g. latest report ID).
 */
interface RoadmapLocalStore {
    fun getLatestReportId(): String?
    fun saveLatestReportId(reportId: String)
    fun clear()
}
