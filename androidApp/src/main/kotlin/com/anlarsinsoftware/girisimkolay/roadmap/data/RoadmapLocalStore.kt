package com.anlarsinsoftware.girisimkolay.roadmap.data

import android.content.Context
import com.anlarsinsoftware.girisimkolay.roadmap.data.source.RoadmapLocalStore

class AndroidRoadmapLocalStore(
    context: Context
) : RoadmapLocalStore {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getLatestReportId(): String? = prefs.getString(KEY_LATEST_REPORT_ID, null)

    override fun saveLatestReportId(reportId: String) {
        prefs.edit().putString(KEY_LATEST_REPORT_ID, reportId).apply()
    }

    override fun clear() {
        prefs.edit().remove(KEY_LATEST_REPORT_ID).apply()
    }

    private companion object {
        const val PREFS_NAME = "girisimkolay_roadmap"
        const val KEY_LATEST_REPORT_ID = "latest_report_id"
    }
}
