package io.sample.smartaccess.app.feature.map

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

internal class GeofenceViewModel(savedStateHandle: SavedStateHandle) :
    ViewModel(), ContainerHost<State, Effect> {

    override val container: Container<State, Effect> = container(
        initialState = State(),
        savedStateHandle = savedStateHandle
    )

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
                if (radius < 50) postSideEffect(Effect.ShowRadiusMinimum)
                else reduce {
                    state.copy(
                        radius = radius,
                        registerEnabled = false,
                        deregisterEnabled = true,
                        radiusInputVisible = false
                    )
                }
            }
            .onFailure { postSideEffect(Effect.ShowRadiusInvalid) }
        postSideEffect(Effect.HideKeyboard)
    }

    fun onDeregister() = intent {
        reduce {
            state.copy(
                registerEnabled = true,
                deregisterEnabled = false,
                radiusInputVisible = true
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
    val radiusInputVisible: Boolean = true,
    val position: LatLng = LatLng(0.0, 0.0),
    val zoom: Float = 18f,
    val radius: Double = 50.0,
    val radiusText: String = "50"
) : Parcelable

internal sealed class Effect {
    object ShowRadiusInvalid : Effect()
    object ShowRadiusMinimum : Effect()
    object HideKeyboard : Effect()
}
