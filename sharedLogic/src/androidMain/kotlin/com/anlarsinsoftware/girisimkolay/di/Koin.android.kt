package com.anlarsinsoftware.girisimkolay.di

import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    // Android specific bindings if any needed in sharedLogic
    // RoadmapLocalStore is provided by androidApp module's appModule for now
}
