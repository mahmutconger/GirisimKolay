package com.anlarsinsoftware.girisimkolay.community.domain.entity

import kotlinx.serialization.Serializable

@Serializable
enum class CommunityAuthorType {
    ENTREPRENEUR,
    EXPERT
}

@Serializable
data class CommunityPost(
    val id: String,
    val authorName: String,
    val content: String,
    val likes: Int,
    val commentsCount: Int,
    val isPinned: Boolean = false,
    val authorType: CommunityAuthorType = CommunityAuthorType.ENTREPRENEUR,
    val isVerifiedExpert: Boolean = false
) {
    val isExpert: Boolean
        get() = authorType == CommunityAuthorType.EXPERT
}
