package io.sample.smartaccess.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import io.sample.smartaccess.app.R
import io.sample.smartaccess.app.core.RootActivity


class AdvertisementService : Service() {

    override fun onCreate() {
        super.onCreate()
        GattServer.create(this)
        GattServer.startAdvertising()
    }

    override fun onDestroy() {
        super.onDestroy()
        GattServer.stopAdvertising()
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val input: String? = intent.getStringExtra("inputExtra")
        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.service_notification_title))
            .setContentText(input)
            .setSmallIcon(R.drawable.baseline_bluetooth_searching_24)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(createServicePendingIntent(this))
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
        startForeground(1, notification)
        return START_NOT_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(CHANNEL_ID, getString(R.string.service_advertising_channel_name), IMPORTANCE_DEFAULT)
        getSystemService(NotificationManager::class.java).createNotificationChannel(serviceChannel)
    }

    private fun createServicePendingIntent(context: Context) =
        PendingIntent.getService(
            context, 0, Intent(context, RootActivity::class.java),
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                PendingIntent.FLAG_CANCEL_CURRENT
            } else {
                PendingIntent.FLAG_MUTABLE
            }
        )

    companion object {
        const val CHANNEL_ID = "SmartAccessAdvertisementServiceChannel"
    }
}