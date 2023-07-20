package io.sample.smartaccess.data

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT
import com.google.android.gms.location.Geofence.NEVER_EXPIRE
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class GeofenceManager(context: Context) {

    private val client by lazy { LocationServices.getGeofencingClient(context) }

    private val geofencingPendingIntent by lazy {
        PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, GeofenceBroadcastReceiver::class.java),
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                PendingIntent.FLAG_CANCEL_CURRENT
            } else {
                PendingIntent.FLAG_MUTABLE
            }
        )
    }

    @SuppressLint("MissingPermission")
    fun registerGeofence(
        key: String,
        location: LatLng,
        radiusInMeters: Float,
        expirationTimeInMillis: Long = NEVER_EXPIRE,
    ) {
        client.addGeofences(createGeofencingRequest(createGeofence(
            key, location, radiusInMeters, expirationTimeInMillis
        )), geofencingPendingIntent)
            .addOnSuccessListener {
                Timber.d("GeofenceManager: registerGeofence: SUCCESS")
            }.addOnFailureListener { exception ->
                Timber.d("GeofenceManager: registerGeofence: Failure\n$exception")
            }
    }

    suspend fun deregisterGeofence() = kotlin.runCatching {
        client.removeGeofences(geofencingPendingIntent).await()
    }

    private fun createGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GEOFENCE_TRANSITION_ENTER)
            addGeofences(listOf(geofence))
        }.build()
    }

    private fun createGeofence(
        key: String,
        location: LatLng,
        radiusInMeters: Float,
        expirationTimeInMillis: Long = NEVER_EXPIRE,
    ): Geofence {
        return Geofence.Builder()
            .setRequestId(key)
            .setCircularRegion(location.latitude, location.longitude, radiusInMeters)
            .setExpirationDuration(expirationTimeInMillis)
            .setTransitionTypes(GEOFENCE_TRANSITION_ENTER or GEOFENCE_TRANSITION_EXIT or GEOFENCE_TRANSITION_DWELL)
            .setLoiteringDelay(TimeUnit.MINUTES.toMillis(2).toInt())
            .build()
    }

}