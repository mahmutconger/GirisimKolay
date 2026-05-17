package com.anlarsinsoftware.girisimkolay.community.domain.repository

import com.anlarsinsoftware.girisimkolay.community.domain.entity.CommunityPost
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun getNetworkPosts(): Flow<List<CommunityPost>>
    fun getExpertPosts(): Flow<List<CommunityPost>>
}
