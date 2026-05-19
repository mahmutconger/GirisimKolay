package com.anlarsinsoftware.girisimkolay.roadmap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anlarsinsoftware.girisimkolay.core.domain.Result
import com.anlarsinsoftware.girisimkolay.core.domain.AppError
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.RoadmapReport
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.RoadmapStep
import com.anlarsinsoftware.girisimkolay.roadmap.domain.repository.RoadmapRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Yol haritası ekranının tüm UI durumunu temsil eden immutable veri modeli.
 */
data class RoadmapUiState(
    val steps: List<RoadmapStep> = emptyList(),
    val latestReport: RoadmapReport? = null,
    val isLoadingSteps: Boolean = false,
    val isGeneratingReport: Boolean = false,
    val isSendingToExpert: Boolean = false,
    val error: AppError? = null
)

/**
 * UI tarafından ele alınması gereken tek seferlik yan etkiler.
 */
sealed class RoadmapEffect {
    data class OpenUrl(val url: String) : RoadmapEffect()
    data class ShowToast(val message: String) : RoadmapEffect()
}

class RoadmapViewModel(
    private val roadmapRepository: RoadmapRepository
) : ViewModel() {

    // ViewModel'in kendi kontrolündeki (loading, error vb.) durumları tutan dahili akış.
    private val _internalState = MutableStateFlow(RoadmapUiState())
    
    // Tek seferlik yan etkileri iletmek için kullanılan kanal.
    private val _effects = Channel<RoadmapEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()
    
    /**
     * Repository'den gelen canlı verileri ve dahili durumları tek bir akışta birleştiriyoruz.
     * Bu sayede UI her zaman atomik ve tutarlı bir state görür.
     */
    val uiState: StateFlow<RoadmapUiState> = combine(
        roadmapRepository.getRoadmapSteps(),
        roadmapRepository.getLatestReport(),
        _internalState
    ) { steps, report, internal ->
        internal.copy(
            steps = steps,
            latestReport = report
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RoadmapUiState(isLoadingSteps = true)
    )

    init {
        refreshData()
    }

    /**
     * Rapor verilerini repository üzerinden tazeler.
     */
    fun refreshData() {
        viewModelScope.launch {
            roadmapRepository.refreshLatestReport()
        }
    }

    /**
     * Mevcut AI sohbet oturumundan yeni bir girişim hazırlık raporu üretir.
     */
    fun generateReport() {
        viewModelScope.launch {
            _internalState.update { it.copy(isGeneratingReport = true, error = null) }
            
            val result = roadmapRepository.generateRoadmapReport()
            
            _internalState.update { state ->
                state.copy(
                    isGeneratingReport = false,
                    error = if (result is Result.Error) result.error else null
                )
            }
        }
    }

    /**
     * Üretilen raporu uzman (mali müşavir) onayına gönderir.
     */
    fun sendToExpert() {
        viewModelScope.launch {
            _internalState.update { it.copy(isSendingToExpert = true, error = null) }
            
            val result = roadmapRepository.sendToExpert()
            
            _internalState.update { state ->
                state.copy(
                    isSendingToExpert = false,
                    error = if (result is Result.Error) result.error else null
                )
            }
        }
    }

    /**
     * Üretilen raporu indirir veya görüntülemek için URL'yi açar.
     */
    fun downloadReport() {
        uiState.value.latestReport?.fileUrl?.let { url ->
            viewModelScope.launch {
                _effects.send(RoadmapEffect.OpenUrl(url))
            }
        }
    }

    /**
     * UI üzerindeki hata mesajını temizler (örn. kullanıcı diyalogu kapattığında).
     */
    fun clearError() {
        _internalState.update { it.copy(error = null) }
    }
}
