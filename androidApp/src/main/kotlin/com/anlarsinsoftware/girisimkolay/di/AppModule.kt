package com.anlarsinsoftware.girisimkolay.di

import com.anlarsinsoftware.girisimkolay.BuildConfig
import com.anlarsinsoftware.girisimkolay.analytics.viewmodel.AnalyticsViewModel
import com.anlarsinsoftware.girisimkolay.auth.data.FirebaseAuthRepository
import com.anlarsinsoftware.girisimkolay.auth.data.FirestoreProfileRepository
import com.anlarsinsoftware.girisimkolay.auth.domain.repository.AuthRepository
import com.anlarsinsoftware.girisimkolay.auth.viewmodel.AuthViewModel
import com.anlarsinsoftware.girisimkolay.calendar.viewmodel.CalendarViewModel
import com.anlarsinsoftware.girisimkolay.chat.data.FirebaseChatRepository
import com.anlarsinsoftware.girisimkolay.chat.data.FirebaseFunctionsChatDataSource
import com.anlarsinsoftware.girisimkolay.chat.data.FirestoreChatHistoryDataSource
import com.anlarsinsoftware.girisimkolay.chat.data.ChatSessionLocalStore
import com.anlarsinsoftware.girisimkolay.chat.domain.repository.ChatRepository
import com.anlarsinsoftware.girisimkolay.chat.viewmodel.ChatViewModel
import com.anlarsinsoftware.girisimkolay.community.viewmodel.CommunityViewModel
import com.anlarsinsoftware.girisimkolay.core.domain.SessionStateStore
import com.anlarsinsoftware.girisimkolay.dashboard.viewmodel.DashboardViewModel
import com.anlarsinsoftware.girisimkolay.firebase.FirebaseRuntimeConfig
import com.anlarsinsoftware.girisimkolay.profile.domain.repository.ProfileRepository
import com.anlarsinsoftware.girisimkolay.roadmap.data.AndroidRoadmapLocalStore
import com.anlarsinsoftware.girisimkolay.roadmap.data.FirebaseFunctionsReportDataSource
import com.anlarsinsoftware.girisimkolay.roadmap.data.FirebaseRoadmapRepository
import com.anlarsinsoftware.girisimkolay.roadmap.data.FirestoreReportDataSource
import com.anlarsinsoftware.girisimkolay.roadmap.data.source.RoadmapLocalStore
import com.anlarsinsoftware.girisimkolay.roadmap.domain.repository.DocumentRepository
import com.anlarsinsoftware.girisimkolay.roadmap.domain.repository.RoadmapRepository
import com.anlarsinsoftware.girisimkolay.roadmap.viewmodel.RoadmapViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/** Android-specific Koin module — registered in GirisimKolayApp */
val appModule = module {

    // ── Firebase singletons ──────────────────────────────────
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseFunctions.getInstance(FirebaseRuntimeConfig.REGION) }
    single<SessionStateStore> { ChatSessionLocalStore(get()) }
    single<RoadmapLocalStore> { AndroidRoadmapLocalStore(get()) }
    single { FirebaseFunctionsChatDataSource(get(), get()) }
    single { FirestoreChatHistoryDataSource(get()) }
    single { FirebaseFunctionsReportDataSource(get(), get()) }
    single { FirestoreReportDataSource(get()) }

    // ── Auth data layer ────────────────────────────────────────
    single<AuthRepository> { FirebaseAuthRepository(get(), get(), get(), get()) }
    single<ProfileRepository> {
        FirestoreProfileRepository(
            auth = get(),
            firestore = get(),
            functions = get(),
            clock = get(),
            logger = get()
        )
    }

    // ── Chat data layer (Firebase native) ──────────────────────
    single<ChatRepository> {
        FirebaseChatRepository(
            auth = get(),
            functionsDataSource = get(),
            historyDataSource = get(),
            sessionStateStore = get(),
            clock = get(),
            idProvider = get(),
            logger = get()
        )
    }

    single<RoadmapRepository> {
        FirebaseRoadmapRepository(
            authRepository = get(),
            chatRepository = get(),
            functionsDataSource = get(),
            reportDataSource = get(),
            roadmapLocalStore = get(),
            clock = get(),
            logger = get()
        )
    }
    single<DocumentRepository> { get<RoadmapRepository>() }

    // ── ViewModels ─────────────────────────────────────────────
    viewModel { AuthViewModel(get()) }
    viewModel { ChatViewModel(get()) }
    viewModel { DashboardViewModel(get()) }
    viewModel { CalendarViewModel(get()) }
    viewModel { AnalyticsViewModel(get()) }
    viewModel { CommunityViewModel(get()) }
    viewModel { RoadmapViewModel(get()) }
}
