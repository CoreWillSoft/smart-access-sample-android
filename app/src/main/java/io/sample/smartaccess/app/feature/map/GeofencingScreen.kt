package io.sample.smartaccess.app.feature.map

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.*
import io.sample.smartaccess.app.R
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
    GeofenceEffectCollector(viewModel)
    Box(
        Modifier.fillMaxSize()
    ) {
        GoogleMapView(
            modifier = Modifier.matchParentSize(),
            modifierBottomSheet = Modifier.align(Alignment.BottomCenter),
            cameraPositionState = cameraPositionState,
            onMapLoaded = viewModel::onMapLoaded,
            state = state,
            onPositionChange = viewModel::onPositionChange,
            onRadiusChange = viewModel::onRadiusChange,
            onLocationLoaded = viewModel::onLocationLoaded,
            onZoomChange = viewModel::onZoomChange,
            onAddClick = viewModel::onAddClick,
            onDeleteClick = viewModel::onDeleteClick,
            onSaveClick = viewModel::onSaveClick
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
                        .background(MaterialTheme.colorScheme.background)
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
    modifierBottomSheet: Modifier = Modifier,
    state: State,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    onMapLoaded: () -> Unit,
    onPositionChange: (LatLng) -> Unit,
    onRadiusChange: (String) -> Unit = {},
    onLocationLoaded: () -> Unit,
    onZoomChange: (Float) -> Unit,
    onAddClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSaveClick: () -> Unit,
    content: @Composable () -> Unit = {}
) {
    val markerState = rememberMarkerState(position = state.position)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                compassEnabled = false,
                mapToolbarEnabled = false,
                zoomControlsEnabled = false
            )
        )
    }
    val mapProperties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL, isMyLocationEnabled = true)) }

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
                    onPositionChange(position)
                    onLocationLoaded()
                }
            }
        }
    }
    LaunchedEffect(markerState.dragState) {
        if (markerState.dragState != DragState.END) return@LaunchedEffect
        cameraPositionState.position =
            CameraPosition.fromLatLngZoom(markerState.position, state.zoom)
        markerState.hideInfoWindow()
        onPositionChange(markerState.position)
    }
    LaunchedEffect(cameraPositionState.position.zoom) {
        onZoomChange(cameraPositionState.position.zoom)
    }

    GoogleMap(
        modifier = modifier.padding(bottom = if (state.bottomBarState is State.BottomBarState.ManagePoint) 60.dp else 100.dp),
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
            Text(it.title ?: stringResource(R.string.marker_text, state.radiusText), color = Color.Black)
        }
        Circle(
            center = state.position,
            fillColor = Color(0xFF54FFD3).copy(alpha = 0.2f),
            strokeColor = Color.Black,
            strokeWidth = 3.0f,
            radius = state.radius,
        )
        content()
    }
    if (state.bottomBarState is State.BottomBarState.ManagePoint)
        MapManagePointComponent(
            modifier = modifierBottomSheet,
            onAddClick = onAddClick,
            onDeleteClick = onDeleteClick
        )
    else
        MapRegisterPointComponent(
            onRadiusChange = onRadiusChange,
            onRegister = onSaveClick,
            radius = state.radiusText,
            modifier = modifierBottomSheet
        )
}

@Composable
private fun MapManagePointComponent(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = Color(0xFFF7F7F6),
                shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
            )
            .height(68.dp)
            .fillMaxWidth(),
    ) {
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 20.dp)
                .clickable { onDeleteClick() }
        )
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 20.dp)
                .clickable { onAddClick() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapRegisterPointComponent(
    modifier: Modifier = Modifier,
    radius: String,
    onRegister: () -> Unit = {},
    onRadiusChange: (String) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color(0xFFF7F7F6), shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
    ) {
        TextField(
            value = radius,
            onValueChange = onRadiusChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = stringResource(R.string.radius_in_meters_text))
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
            )
        )
        Button(
            onClick = onRegister,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(text = stringResource(R.string.save))
        }
    }


}