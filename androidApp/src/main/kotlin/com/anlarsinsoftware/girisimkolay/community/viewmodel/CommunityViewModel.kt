package com.anlarsinsoftware.girisimkolay.community.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anlarsinsoftware.girisimkolay.community.domain.entity.CommunityPost
import com.anlarsinsoftware.girisimkolay.community.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CommunityViewModel(
    communityRepository: CommunityRepository
) : ViewModel() {

    val networkPosts: StateFlow<List<CommunityPost>> = communityRepository.getNetworkPosts()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val expertPosts: StateFlow<List<CommunityPost>> = communityRepository.getExpertPosts()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
