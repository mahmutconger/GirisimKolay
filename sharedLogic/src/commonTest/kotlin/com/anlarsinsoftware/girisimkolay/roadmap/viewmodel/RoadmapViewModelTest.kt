package com.anlarsinsoftware.girisimkolay.roadmap.viewmodel

import com.anlarsinsoftware.girisimkolay.core.domain.Result
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.ApprovalStatus
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.RoadmapReport
import com.anlarsinsoftware.girisimkolay.roadmap.domain.entity.RoadmapStep
import com.anlarsinsoftware.girisimkolay.roadmap.domain.repository.RoadmapRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class RoadmapViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeRoadmapRepository
    private lateinit var viewModel: RoadmapViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeRoadmapRepository()
        viewModel = RoadmapViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load steps and reports from repository`() = runTest {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect {} }
        val steps = listOf(RoadmapStep("1", "Title", "Desc", false, true))
        repository.stepsFlow.value = steps
        
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals(steps, state.steps)
        assertNull(state.latestReport)
        collectJob.cancel()
    }

    @Test
    fun `generateReport success should update state and clear error`() = runTest {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect {} }
        val report = RoadmapReport("r1", "u1", "s1", "Title", "Summary", "url", 123456789L, ApprovalStatus.IDLE, emptyList())
        repository.generateResult = Result.Success(report)
        
        viewModel.generateReport()
        
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals(false, state.isGeneratingReport)
        assertNull(state.error)
        assertEquals(report, state.latestReport)
        collectJob.cancel()
    }

    @Test
    fun `generateReport error should update state with error`() = runTest {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect {} }
        repository.generateResult = Result.Error(message = "Failed", code = "test_error")
        
        viewModel.generateReport()
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals(false, state.isGeneratingReport)
        assertNotNull(state.error)
        assertEquals("test_error", state.error.code)
        collectJob.cancel()
    }

    @Test
    fun `clearError should set error to null`() = runTest {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect {} }
        repository.generateResult = Result.Error(message = "Failed", code = "test_error")
        viewModel.generateReport()
        advanceUntilIdle()
        
        assertNotNull(viewModel.uiState.value.error)
        
        viewModel.clearError()
        advanceUntilIdle()
        
        assertNull(viewModel.uiState.value.error)
        collectJob.cancel()
    }

    @Test
    fun `downloadReport should emit OpenUrl effect when report exists`() = runTest {
        val effects = mutableListOf<RoadmapEffect>()
        val collectEffectsJob = backgroundScope.launch { viewModel.effects.collect { effects.add(it) } }
        val collectStateJob = backgroundScope.launch { viewModel.uiState.collect {} }
        
        val report = RoadmapReport("r1", "u1", "s1", "Title", "Summary", "url", 123456789L, ApprovalStatus.IDLE, emptyList())
        repository.reportFlow.value = report
        
        // Wait for state to update
        runCurrent()
        advanceUntilIdle()
        
        assertNotNull(viewModel.uiState.value.latestReport, "Latest report should not be null in state")
        
        viewModel.downloadReport()
        runCurrent()
        advanceUntilIdle()
        
        assertEquals(1, effects.size, "Should have emitted exactly one effect")
        assertEquals(RoadmapEffect.OpenUrl("url"), effects[0])
        
        collectEffectsJob.cancel()
        collectStateJob.cancel()
    }
}

class FakeRoadmapRepository : RoadmapRepository {
    val stepsFlow = MutableStateFlow<List<RoadmapStep>>(emptyList())
    val reportFlow = MutableStateFlow<RoadmapReport?>(null)
    
    var generateResult: Result<RoadmapReport> = Result.Error("Not implemented")
    var sendResult: Result<ApprovalStatus> = Result.Error("Not implemented")

    override fun getRoadmapSteps(): Flow<List<RoadmapStep>> = stepsFlow
    override fun getLatestReport(): Flow<RoadmapReport?> = reportFlow
    
    override suspend fun refreshLatestReport(forceRefresh: Boolean): Result<RoadmapReport?> = Result.Success(null)
    
    override suspend fun generateRoadmapReport(): Result<RoadmapReport> {
        val res = generateResult
        if (res is Result.Success) {
            reportFlow.value = res.data
        }
        return res
    }
    
    override suspend fun sendToExpert(): Result<ApprovalStatus> = sendResult
}
