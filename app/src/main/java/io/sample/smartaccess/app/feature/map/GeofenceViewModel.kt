package io.sample.smartaccess.app.feature.map

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.sample.smartaccess.data.GeofenceManager
import com.google.android.gms.maps.model.LatLng
import io.sample.smartaccess.domain.GeofenceTransitionChannel
import io.sample.smartaccess.domain.GeofenceTransitionSession
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.parcelize.Parcelize
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

private const val GEOFENCE_KEY = "geofence1"
private const val MIN_RADIUS = 50

internal class GeofenceViewModel(
    savedStateHandle: SavedStateHandle,
    private val geofenceManager: GeofenceManager,
    private val geofenceTransitionChannel: GeofenceTransitionChannel
) :
    ViewModel(), ContainerHost<State, Effect> {

    override val container: Container<State, Effect> = container(
        initialState = State(),
        savedStateHandle = savedStateHandle
    ) {
        observeGeofenceTransition()
    }

    private fun observeGeofenceTransition() = intent {
        geofenceTransitionChannel.filterIsInstance<GeofenceTransitionSession.Exit>().collect {
            reduce { state.copy(
                registerEnabled = true,
                deregisterEnabled = false,
                radiusInputEnabled = true
            ) }
        }
    }

    fun onMapLoaded() = intent {
        reduce { state.copy(mapLoaded = true) }
    }

    fun onPositionChange(position: LatLng) = intent {
        reduce { state.copy(position = position) }
    }

    fun onZoomChange(zoom: Float) = intent {
        reduce { state.copy(zoom = zoom) }
    }

    fun onLocationLoaded() = intent {
        reduce { state.copy(currentLocationLoaded = true) }
    }

    fun onRegister() = intent {
        runCatching { state.radiusText.toDouble() }
            .onSuccess { radius ->
                if (radius < MIN_RADIUS) postSideEffect(Effect.ShowRadiusMinimum)
                else {
                    geofenceManager.registerGeofence(GEOFENCE_KEY, state.position, radius.toFloat())
                    reduce {
                        state.copy(
                            radius = radius,
                            registerEnabled = false,
                            deregisterEnabled = true,
                            radiusInputEnabled = false
                        )
                    }
                }
            }
            .onFailure { postSideEffect(Effect.ShowRadiusInvalid) }
        postSideEffect(Effect.HideKeyboard)
    }

    fun onDeregister() = intent {
        geofenceManager.deregisterGeofence()
        postSideEffect(Effect.DeregisterGeofence)
        reduce {
            state.copy(
                registerEnabled = true,
                deregisterEnabled = false,
                radiusInputEnabled = true
            )
        }
    }

    fun onRadiusChange(radius: String) = intent {
        reduce { state.copy(radiusText = radius) }
    }
}

@Parcelize
internal data class State(
    val mapLoaded: Boolean = false,
    val currentLocationLoaded: Boolean = false,
    val registerEnabled: Boolean = true,
    val deregisterEnabled: Boolean = false,
    val radiusInputEnabled: Boolean = true,
    val position: LatLng = LatLng(0.0, 0.0),
    val zoom: Float = 16f,
    val radius: Double = 200.0,
    val radiusText: String = "200"
) : Parcelable

internal sealed class Effect {
    object ShowRadiusInvalid : Effect()
    object ShowRadiusMinimum : Effect()
    object HideKeyboard : Effect()
    object DeregisterGeofence : Effect()
}
