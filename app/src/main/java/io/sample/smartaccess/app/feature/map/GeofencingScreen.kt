package io.sample.smartaccess.app.feature.map

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.DragState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

private const val TAG = "GeofencingScreen"
val defaultCameraPosition = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 11f)

@Composable
internal fun GeofencingScreen() {
    val viewModel = koinViewModel<GeofenceViewModel>()
    val state by viewModel.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = defaultCameraPosition
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMapView(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            onMapLoaded = viewModel::onMapLoaded,
            viewModel = viewModel
        )
        if (!state.mapLoaded && !state.currentLocationLoaded) {
            AnimatedVisibility(
                modifier = Modifier
                    .matchParentSize(),
                visible = true,
                enter = EnterTransition.None,
                exit = fadeOut()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(MaterialTheme.colors.background)
                        .wrapContentSize()
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun GoogleMapView(
    modifier: Modifier = Modifier,
    viewModel: GeofenceViewModel,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    onMapLoaded: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    val state by viewModel.collectAsState()
    val markerState = rememberMarkerState(position = state.position)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val uiSettings by remember { mutableStateOf(MapUiSettings(compassEnabled = false)) }
    val mapProperties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL)) }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            val result = locationClient.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()
            result?.let { fetchedLocation ->
                withContext(Dispatchers.Main) {
                    val position = LatLng(fetchedLocation.latitude, fetchedLocation.longitude)
                    markerState.position = position
                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(markerState.position, state.zoom)
                    viewModel.onPositionChange(position = position)
                    viewModel.onLocationLoaded()
                }
            }
        }
    }
    LaunchedEffect(markerState.dragState) {
        if (markerState.dragState != DragState.END) return@LaunchedEffect
        cameraPositionState.position =
            CameraPosition.fromLatLngZoom(markerState.position, state.zoom)
        markerState.hideInfoWindow()
        viewModel.onPositionChange(position = markerState.position)
    }
    LaunchedEffect(cameraPositionState.position.zoom) {
        viewModel.onZoomChange(cameraPositionState.position.zoom)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = uiSettings,
        onMapLoaded = onMapLoaded
    ) {
        // Drawing on the map is accomplished with a child-based API

        MarkerInfoWindowContent(
            state = markerState,
            draggable = true,
        ) {
            Text(it.title ?: "Geofence", color = Color.Red)
        }
        Circle(
            center = state.position,
            fillColor = MaterialTheme.colors.secondary.copy(alpha = 0.2f),
            strokeColor = MaterialTheme.colors.secondaryVariant,
            strokeWidth = 3.0f,
            radius = state.radius,
        )
        content()
    }
    MapTopComponent(
        registerEnabled = state.registerEnabled,
        deRegisterEnabled = state.deregisterEnabled,
        onRadiusChange = viewModel::onRadiusChange,
        onRegister = viewModel::onRegister,
        onDeregister = viewModel::onDeregister,
        radius = state.radiusText,
        radiusInputVisible = state.radiusInputVisible
    )
}

@Composable
private fun MapTopComponent(
    modifier: Modifier = Modifier,
    registerEnabled: Boolean,
    deRegisterEnabled: Boolean,
    radius: String,
    radiusInputVisible: Boolean,
    onRegister: () -> Unit = {},
    onDeregister: () -> Unit = {},
    onRadiusChange: (String) -> Unit = {}
) {
    Column(modifier = modifier) {
        if (radiusInputVisible) {
            TextField(value = radius, onValueChange = onRadiusChange)
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onRegister,
                enabled = registerEnabled,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Register area")
            }
            Button(
                onClick = onDeregister,
                enabled = deRegisterEnabled,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Deregister area")
            }
        }
    }


}