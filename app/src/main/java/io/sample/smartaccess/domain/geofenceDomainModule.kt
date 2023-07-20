package io.sample.smartaccess.domain

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.dsl.module

internal val geofenceDomainModule = module {
    single<GeofenceTransitionChannel> {
        MutableSharedFlow(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }
}