package com.anlarsinsoftware.girisimkolay.chat.data

import android.content.Context
import com.anlarsinsoftware.girisimkolay.core.domain.SessionStateStore

class ChatSessionLocalStore(
    context: Context
) : SessionStateStore {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getActiveSessionId(): String? = prefs.getString(KEY_ACTIVE_SESSION_ID, null)

    override fun saveActiveSessionId(sessionId: String) {
        prefs.edit().putString(KEY_ACTIVE_SESSION_ID, sessionId).apply()
    }

    override fun clear() {
        prefs.edit().remove(KEY_ACTIVE_SESSION_ID).apply()
    }

    private companion object {
        const val PREFS_NAME = "girisimkolay_chat_session"
        const val KEY_ACTIVE_SESSION_ID = "active_session_id"
    }
}
