package io.sample.smartaccess.data.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic.*
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import io.sample.smartaccess.app.BuildConfig
import io.sample.smartaccess.data.tunnel.Tunnel
import no.nordicsemi.android.ble.BleServerManager
import no.nordicsemi.android.ble.observer.ServerObserver
import java.util.*

internal val SERVICE_UUID: UUID = UUID.fromString("3c25c1cf-b62c-4f3b-8205-c5e078b7d61d")
internal val CHARACTERISTIC_UUID: UUID = UUID.fromString("3c254203-b62c-4f3b-8205-c5e078b7d61d")

class GattServerManager(private val context: Context) : BleServerManager(context), ServerObserver {

    private val gattCharacteristic = sharedCharacteristic(
        CHARACTERISTIC_UUID,
        PROPERTY_NOTIFY or PROPERTY_WRITE_NO_RESPONSE,
        PERMISSION_READ or PERMISSION_WRITE,
    )

    private val gattService = service(SERVICE_UUID, gattCharacteristic)

    private val serverConnections = mutableMapOf<String, Tunnel>()

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
        serverConnections[device.address] = Tunnel.make(device, this, context, gattCharacteristic).apply(Tunnel::connect)
    }

    override fun onDeviceDisconnectedFromServer(device: BluetoothDevice) {
        serverConnections.remove(device.address)?.closeConnection()
    }
}