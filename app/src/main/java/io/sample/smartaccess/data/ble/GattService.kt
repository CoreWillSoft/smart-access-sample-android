package io.sample.smartaccess.data.ble

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED
import android.bluetooth.BluetoothAdapter.STATE_ON
import android.bluetooth.BluetoothAdapter.STATE_OFF
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import io.sample.smartaccess.app.R
import io.sample.smartaccess.app.core.RootActivity
import org.koin.core.context.GlobalContext

class GattService : Service() {

    private var serverManager: GattServerManager? = null

    private lateinit var bluetoothObserver: BroadcastReceiver

    private val bleAdvertiseCallback: Callback by lazy { Callback() }

    override fun onCreate() {
        super.onCreate()
        prepareNotifications()
        bluetoothObserver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    ACTION_STATE_CHANGED -> {
                        when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                            STATE_ON -> enableBleServices()
                            STATE_OFF -> disableBleServices()
                        }
                    }
                }
            }
        }
        registerReceiver(bluetoothObserver, IntentFilter(ACTION_STATE_CHANGED))

        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        if (bluetoothManager.adapter?.isEnabled == true) enableBleServices()
    }

    override fun onDestroy() {
        super.onDestroy()
        disableBleServices()
        unregisterReceiver(bluetoothObserver)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    @SuppressLint("MissingPermission")
    private fun enableBleServices() {
        serverManager = GattServerManager(this).also(GattServerManager::open)
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val gattAdvertiseFactory = GlobalContext.get().get<GattAdvertiseFactory>()
        bluetoothManager.adapter.bluetoothLeAdvertiser?.startAdvertising(
            gattAdvertiseFactory.settings(),
            gattAdvertiseFactory.data(),
            bleAdvertiseCallback
        )
    }

    @SuppressLint("MissingPermission")
    private fun disableBleServices() {
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter.bluetoothLeAdvertiser?.stopAdvertising(bleAdvertiseCallback)
        serverManager?.close()
        serverManager = null
    }

    private fun createServicePendingIntent() =
        PendingIntent.getService(
            this, 0, Intent(this, RootActivity::class.java),
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                PendingIntent.FLAG_CANCEL_CURRENT
            } else {
                PendingIntent.FLAG_MUTABLE
            }
        )

    private fun prepareNotifications() {
        val notificationChannel = NotificationChannel(
            GattService::class.java.simpleName,
            resources.getString(R.string.gatt_service_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationService =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationService.createNotificationChannel(notificationChannel)

        val notification = NotificationCompat.Builder(this, GattService::class.java.simpleName)
            .setSmallIcon(R.drawable.baseline_bluetooth_searching_24)
            .setContentTitle(resources.getString(R.string.gatt_service_name))
            .setContentText(resources.getString(R.string.gatt_service_running_notification))
            .setAutoCancel(true)
            .setContentIntent(createServicePendingIntent())
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        startForeground(1, notification.build())
    }

    private class Callback : AdvertiseCallback()

}