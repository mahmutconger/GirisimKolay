package com.anlarsinsoftware.girisimkolay.roadmap.data.source

import platform.Foundation.NSUserDefaults

class IosRoadmapLocalStore : RoadmapLocalStore {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun getLatestReportId(): String? = defaults.stringForKey(KEY_LATEST_REPORT_ID)

    override fun saveLatestReportId(reportId: String) {
        defaults.setObject(reportId, forKey = KEY_LATEST_REPORT_ID)
    }

    override fun clear() {
        defaults.removeObjectForKey(KEY_LATEST_REPORT_ID)
    }

    private companion object {
        const val KEY_LATEST_REPORT_ID = "latest_report_id"
    }
}
