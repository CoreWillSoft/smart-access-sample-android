package io.sample.smartaccess.domain

import kotlinx.coroutines.flow.MutableSharedFlow

typealias GeofenceTransitionChannel = MutableSharedFlow<GeofenceTransitionSession>

sealed class GeofenceTransitionSession {
    object Enter : GeofenceTransitionSession()
    object Exit : GeofenceTransitionSession()
    object Dwell : GeofenceTransitionSession()
}