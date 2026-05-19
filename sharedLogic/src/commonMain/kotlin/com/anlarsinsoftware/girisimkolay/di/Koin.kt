package com.anlarsinsoftware.girisimkolay.di

import com.anlarsinsoftware.girisimkolay.chat.data.repository.MockChatRepository
import com.anlarsinsoftware.girisimkolay.chat.domain.usecase.SendChatMessage
import com.anlarsinsoftware.girisimkolay.chat.domain.repository.ChatRepository
import com.anlarsinsoftware.girisimkolay.core.domain.Clock
import com.anlarsinsoftware.girisimkolay.core.domain.DefaultClock
import com.anlarsinsoftware.girisimkolay.core.domain.DefaultIdProvider
import com.anlarsinsoftware.girisimkolay.core.domain.IdProvider
import com.anlarsinsoftware.girisimkolay.core.domain.Logger
import com.anlarsinsoftware.girisimkolay.core.domain.NoopLogger
import com.anlarsinsoftware.girisimkolay.dashboard.data.repository.MockDashboardRepository
import com.anlarsinsoftware.girisimkolay.dashboard.domain.repository.NewsRepository
import com.anlarsinsoftware.girisimkolay.calendar.data.repository.MockCalendarRepository
import com.anlarsinsoftware.girisimkolay.calendar.domain.repository.CalendarRepository
import com.anlarsinsoftware.girisimkolay.analytics.data.repository.MockAnalyticsRepository
import com.anlarsinsoftware.girisimkolay.analytics.domain.repository.AnalyticsRepository
import com.anlarsinsoftware.girisimkolay.community.data.repository.MockCommunityRepository
import com.anlarsinsoftware.girisimkolay.community.domain.repository.CommunityRepository
import com.anlarsinsoftware.girisimkolay.profile.data.repository.MockProfileRepository
import com.anlarsinsoftware.girisimkolay.profile.domain.repository.ProfileRepository
import com.anlarsinsoftware.girisimkolay.profile.domain.usecase.LoadProfile
import com.anlarsinsoftware.girisimkolay.profile.domain.usecase.SaveProfile
import com.anlarsinsoftware.girisimkolay.roadmap.data.repository.LiveRoadmapRepository
import com.anlarsinsoftware.girisimkolay.roadmap.data.source.RoadmapLocalStore
import com.anlarsinsoftware.girisimkolay.roadmap.domain.repository.DocumentRepository
import com.anlarsinsoftware.girisimkolay.roadmap.domain.repository.RoadmapRepository
import com.anlarsinsoftware.girisimkolay.roadmap.viewmodel.RoadmapViewModel
import com.anlarsinsoftware.girisimkolay.auth.domain.repository.AuthRepository
import com.anlarsinsoftware.girisimkolay.core.domain.BearerTokenProvider
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            coreModule,
            chatModule,
            profileModule,
            dashboardModule,
            calendarModule,
            analyticsModule,
            communityModule,
            roadmapModule,
            platformModule()
        )
    }

// called by iOS etc
fun initKoin() = initKoin {}

val coreModule = module {
    single<Clock> { DefaultClock }
    single<IdProvider> { DefaultIdProvider }
    single<Logger> { NoopLogger }
}

val chatModule = module {
    single<ChatRepository> { MockChatRepository(get(), get()) }
    factory { SendChatMessage(get()) }
}

val profileModule = module {
    single<ProfileRepository> { MockProfileRepository(get()) }
    factory { LoadProfile(get()) }
    factory { SaveProfile(get()) }
}

val dashboardModule = module {
    single<NewsRepository> { MockDashboardRepository() }
}

val calendarModule = module {
    single<CalendarRepository> { MockCalendarRepository(get()) }
}

val analyticsModule = module {
    single<AnalyticsRepository> { MockAnalyticsRepository() }
}

val communityModule = module {
    single<CommunityRepository> { MockCommunityRepository() }
}

val roadmapModule = module {
    single<RoadmapRepository> {
        LiveRoadmapRepository(
            authRepository = get(),
            httpClient = get(),
            baseUrl = "https://api.girisimkolay.com", // TODO: Move to config
            chatRepository = get(),
            authTokenProvider = get(),
            roadmapLocalStore = get(),
            clock = get(),
            logger = get()
        )
    }
    single<DocumentRepository> { get<RoadmapRepository>() }
    factoryOf(::RoadmapViewModel)
}

expect fun platformModule(): Module
