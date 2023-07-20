package io.sample.smartaccess.app.feature.map

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

internal val geofenceUiModule = module {
    viewModelOf(::GeofenceViewModel)
}