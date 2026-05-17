package com.anlarsinsoftware.girisimkolay.community.domain.usecase

import com.anlarsinsoftware.girisimkolay.community.domain.repository.CommunityRepository

class LoadCommunity(private val repository: CommunityRepository) {
    operator fun invoke() = repository.getNetworkPosts()
}
