package io.sample.smartaccess.data.geofense

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT
import com.google.android.gms.location.GeofencingEvent
import io.sample.smartaccess.app.R
import io.sample.smartaccess.app.core.RootActivity
import io.sample.smartaccess.data.ble.GattService
import io.sample.smartaccess.domain.GeofenceTransitionChannel
import io.sample.smartaccess.domain.GeofenceTransitionSession
import org.koin.core.context.GlobalContext

private const val CHANNEL_ID = "12643"
internal const val NOTIFICATION_BROADCAST_ID = 7549

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent?) {
        val geofencingEvent = intent?.let { GeofencingEvent.fromIntent(it) } ?: return
        if (geofencingEvent.hasError()) return
        createNotificationChannel(context)
        when (geofencingEvent.geofenceTransition) {
            GEOFENCE_TRANSITION_ENTER -> handleEntering(context, geofencingEvent)
            GEOFENCE_TRANSITION_EXIT -> handleExit(context)
            GEOFENCE_TRANSITION_DWELL -> handleDwell(context, geofencingEvent)
            else -> {}
        }

    }

    private fun handleEntering(context: Context, event: GeofencingEvent) {
        val channel = GlobalContext.get().get<GeofenceTransitionChannel>()
        channel.tryEmit(GeofenceTransitionSession.Enter)
        showNotification(context, event)
        val serviceIntent = Intent(context, GattService::class.java)
        serviceIntent.putExtra("inputExtra", context.getString(R.string.service_advertising_text))
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    private fun handleExit(context: Context) {
        val channel = GlobalContext.get().get<GeofenceTransitionChannel>()
        channel.tryEmit(GeofenceTransitionSession.Exit)
        val serviceIntent = Intent(context, GattService::class.java)
        context.stopService(serviceIntent)
        with(NotificationManagerCompat.from(context)) {
            cancel(NOTIFICATION_BROADCAST_ID)
        }
    }

    private fun handleDwell(context: Context, event: GeofencingEvent) {
        showNotification(context, event)
        val channel = GlobalContext.get().get<GeofenceTransitionChannel>()
        channel.tryEmit(GeofenceTransitionSession.Dwell)
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context, geofencingEvent: GeofencingEvent) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_autorenew_24)
            .setContentTitle("Smart Access")
            .setContentText("Geofences triggered : ${mapTransition(geofencingEvent)}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(createGeofencingPendingIntent(context))
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(NOTIFICATION_BROADCAST_ID, builder.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        val name = "SmartAccess Channel"
        val descriptionText = "SmartAccess Channel Test description"
        val channel = NotificationChannel(CHANNEL_ID, name, IMPORTANCE_DEFAULT).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun mapTransition(event: GeofencingEvent): String = when (event.geofenceTransition) {
        GEOFENCE_TRANSITION_ENTER -> "Entering to area"
        GEOFENCE_TRANSITION_EXIT -> "Exiting from area"
        GEOFENCE_TRANSITION_DWELL -> "Staying in area"
        else -> "Unknown transition"
    }

    private fun createGeofencingPendingIntent(context: Context) =
        PendingIntent.getActivity(
            context, 0, Intent(context, RootActivity::class.java),
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                PendingIntent.FLAG_CANCEL_CURRENT
            } else {
                PendingIntent.FLAG_MUTABLE
            }
        )
}