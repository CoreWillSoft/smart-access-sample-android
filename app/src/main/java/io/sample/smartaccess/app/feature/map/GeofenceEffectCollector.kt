package io.sample.smartaccess.app.feature.map

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.core.app.NotificationManagerCompat
import io.sample.smartaccess.app.R
import io.sample.smartaccess.data.ble.GattService
import io.sample.smartaccess.data.geofense.NOTIFICATION_BROADCAST_ID
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun GeofenceEffectCollector(
    viewModel: GeofenceViewModel
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    viewModel.collectSideEffect { effect ->
        when(effect) {
            Effect.DeregisterGeofence -> stopServiceAndNotification(context)
            Effect.ShowRadiusInvalid -> Toast.makeText(context, R.string.radius_invalid_error_text, Toast.LENGTH_SHORT).show()
            Effect.ShowRadiusMinimum -> Toast.makeText(context, R.string.radius_invalid_error_min_text, Toast.LENGTH_SHORT).show()
            Effect.HideKeyboard -> keyboardController?.hide()
        }
    }
}

private fun stopServiceAndNotification(context: Context) {
    val serviceIntent = Intent(context, GattService::class.java)
    context.stopService(serviceIntent)
    NotificationManagerCompat.from(context).cancel(NOTIFICATION_BROADCAST_ID)
}