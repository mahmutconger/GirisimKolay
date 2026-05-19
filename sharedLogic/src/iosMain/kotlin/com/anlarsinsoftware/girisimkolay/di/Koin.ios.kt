package com.anlarsinsoftware.girisimkolay.di

import com.anlarsinsoftware.girisimkolay.roadmap.data.source.IosRoadmapLocalStore
import com.anlarsinsoftware.girisimkolay.roadmap.data.source.RoadmapLocalStore
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<RoadmapLocalStore> { IosRoadmapLocalStore() }
}
