package io.sample.smartaccess.app.feature.map

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.sample.smartaccess.data.geofense.GeofenceManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

private const val GEOFENCE_KEY = "geofence1"
private const val MIN_RADIUS = 50

internal class GeofenceViewModel(
    savedStateHandle: SavedStateHandle,
    private val geofenceManager: GeofenceManager
) :
    ViewModel(), ContainerHost<State, Effect> {

    override val container: Container<State, Effect> = container(
        initialState = State(),
        savedStateHandle = savedStateHandle
    ) {

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

    fun onSaveClick() = intent {
        runCatching { state.radiusText.toDouble() }
            .onSuccess { radius ->
                if (radius < MIN_RADIUS) postSideEffect(Effect.ShowRadiusMinimum)
                else {
                    geofenceManager.deregisterGeofence()
                    geofenceManager.registerGeofence(GEOFENCE_KEY, state.position, radius.toFloat())
                    reduce { state.copy(radius = radius, bottomBarState = State.BottomBarState.ManagePoint) }
                }
            }
            .onFailure { postSideEffect(Effect.ShowRadiusInvalid) }
        postSideEffect(Effect.HideKeyboard)
    }

    fun onDeleteClick() = intent {
        geofenceManager.deregisterGeofence()
        postSideEffect(Effect.DeregisterGeofence)
        reduce { state.copy(bottomBarState = State.BottomBarState.AddPoint) }
    }

    fun onAddClick() = intent {
        reduce {
            state.copy(bottomBarState = State.BottomBarState.AddPoint)
        }
    }

    @OptIn(OrbitExperimental::class)
    fun onRadiusChange(radius: String) = blockingIntent {
        reduce { state.copy(radiusText = radius) }
    }
}

@Parcelize
internal data class State(
    val mapLoaded: Boolean = false,
    val currentLocationLoaded: Boolean = false,
    val bottomBarState: BottomBarState = BottomBarState.AddPoint,
    val position: LatLng = LatLng(0.0, 0.0),
    val zoom: Float = 16f,
    val radius: Double = 200.0,
    val radiusText: String = radius.toInt().toString()
) : Parcelable {

    @Parcelize
    sealed class BottomBarState : Parcelable {
        object AddPoint : BottomBarState()
        object ManagePoint : BottomBarState()
    }
}

internal sealed class Effect {
    object ShowRadiusInvalid : Effect()
    object ShowRadiusMinimum : Effect()
    object HideKeyboard : Effect()
    object DeregisterGeofence : Effect()
}
