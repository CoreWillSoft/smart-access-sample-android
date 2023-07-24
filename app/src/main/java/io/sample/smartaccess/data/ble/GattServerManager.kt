package io.sample.smartaccess.data.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic.PROPERTY_NOTIFY
import android.bluetooth.BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE
import android.bluetooth.BluetoothGattCharacteristic.PERMISSION_READ
import android.bluetooth.BluetoothGattCharacteristic.PERMISSION_WRITE
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import io.sample.smartaccess.app.BuildConfig
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.BleServerManager
import no.nordicsemi.android.ble.callback.DataReceivedCallback
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.observer.ServerObserver
import java.util.*

internal val SERVICE_UUID: UUID = UUID.fromString("3c25c1cf-b62c-4f3b-8205-c5e078b7d61d")
internal val CHARACTERISTIC_UUID: UUID = UUID.fromString("3c254203-b62c-4f3b-8205-c5e078b7d61d")

class GattServerManager(val context: Context) : BleServerManager(context), ServerObserver {

    private val gattCharacteristic = sharedCharacteristic(
        CHARACTERISTIC_UUID,
        PROPERTY_NOTIFY or PROPERTY_WRITE_NO_RESPONSE,
        PERMISSION_READ or PERMISSION_WRITE,
    )

    private val gattService = service(SERVICE_UUID, gattCharacteristic)

    private val serverConnections = mutableMapOf<String, ServerConnection>()

    override fun log(priority: Int, message: String) {
        if (BuildConfig.DEBUG || priority == Log.ERROR) {
            Log.println(priority, "gatt-service", message)
        }
    }

    override fun initializeServer(): List<BluetoothGattService> = listOf(gattService).also { setServerObserver(this) }

    override fun onServerReady() {
        log(Log.INFO, "Gatt server ready")
    }

    override fun onDeviceConnectedToServer(device: BluetoothDevice) {
        serverConnections[device.address] = ServerConnection(this).apply {
            connectDevice(device)
            send(byteArrayOf(0x01))
        }
    }

    override fun onDeviceDisconnectedFromServer(device: BluetoothDevice) {
        serverConnections.remove(device.address)?.close()
    }

    /*
     * Manages the state of an individual server connection (there can be many of these)
     */
    inner class ServerConnection(server: BleServerManager) : BleManager(context), DataReceivedCallback {

        init {
            useServer(server)
            readCharacteristic(gattCharacteristic)
                .with(this)
                .enqueue()
        }

        fun connectDevice(device: BluetoothDevice) {
            connect(device).enqueue()
        }

        fun send(value: ByteArray) {
            sendNotification(gattCharacteristic, value).enqueue()
        }

        override fun log(priority: Int, message: String) {
            this@GattServerManager.log(priority, message)
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            return true
        }

        override fun onServicesInvalidated() {
            // This is the place to nullify characteristics obtained above.
        }

        override fun onDataReceived(device: BluetoothDevice, data: Data) {
            log(Log.DEBUG, data.value.toString())
        }
    }
}