package com.anlarsinsoftware.girisimkolay.roadmap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anlarsinsoftware.girisimkolay.core.domain.Result
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.RoadmapReport
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.RoadmapStep
import com.anlarsinsoftware.girisimkolay.roadmap.domain.repository.RoadmapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RoadmapViewModel(
    private val roadmapRepository: RoadmapRepository
) : ViewModel() {

    val steps: StateFlow<List<RoadmapStep>> = roadmapRepository.getRoadmapSteps()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val latestReport: StateFlow<RoadmapReport?> = roadmapRepository.getLatestReport()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _isGeneratingPdf = MutableStateFlow(false)
    val isGeneratingPdf = _isGeneratingPdf.asStateFlow()

    private val _isSendingToExpert = MutableStateFlow(false)
    val isSendingToExpert = _isSendingToExpert.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            roadmapRepository.refreshLatestReport()
        }
    }

    fun generatePdf() {
        viewModelScope.launch {
            _isGeneratingPdf.value = true
            when (val result = roadmapRepository.generateRoadmapReport()) {
                is Result.Error -> _errorMessage.value = result.message
                is Result.Success -> _errorMessage.value = null
                Result.Loading -> Unit
            }
            _isGeneratingPdf.value = false
        }
    }

    fun sendToExpert() {
        viewModelScope.launch {
            _isSendingToExpert.value = true
            when (val result = roadmapRepository.sendToExpert()) {
                is Result.Error -> _errorMessage.value = result.message
                is Result.Success -> _errorMessage.value = null
                Result.Loading -> Unit
            }
            _isSendingToExpert.value = false
        }
    }
}
