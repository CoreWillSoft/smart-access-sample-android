package io.sample.smartaccess.data.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic.PERMISSION_READ
import android.bluetooth.BluetoothGattCharacteristic.PERMISSION_WRITE
import android.bluetooth.BluetoothGattCharacteristic.PROPERTY_NOTIFY
import android.bluetooth.BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import io.sample.smartaccess.data.tunnel.Tunnel
import no.nordicsemi.android.ble.BleServerManager
import no.nordicsemi.android.ble.observer.ServerObserver
import java.util.UUID

internal val SERVICE_UUID: UUID = UUID.fromString("49575abc-26e1-11ee-be56-0242ac120002")
internal val RECEIVER_CHARACTERISTIC_UUID: UUID = UUID.fromString("49575d8c-26e1-11ee-be56-0242ac120002")
internal val BROADCASTER_CHARACTERISTIC_UUID: UUID = UUID.fromString("49575f08-26e1-11ee-be56-0242ac120002")

internal class GattServerManager(private val context: Context) : BleServerManager(context), ServerObserver {

    private val receiverCharacteristic = sharedCharacteristic(
        RECEIVER_CHARACTERISTIC_UUID,
        PROPERTY_WRITE_NO_RESPONSE,
        PERMISSION_WRITE,
    )

    private val broadcasterCharacteristic = sharedCharacteristic(
        BROADCASTER_CHARACTERISTIC_UUID,
        PROPERTY_NOTIFY,
        PERMISSION_READ,
    )

    private val gattService = service(SERVICE_UUID, receiverCharacteristic, broadcasterCharacteristic)

    private var tunnel: Tunnel? = null

    override fun initializeServer(): List<BluetoothGattService> = listOf(gattService).also { setServerObserver(this) }

    override fun onServerReady() {
        log(Log.INFO, "Gatt server ready")
    }

    override fun onDeviceConnectedToServer(device: BluetoothDevice) {
        if (tunnel != null) return
        tunnel = Tunnel.make(device, this, context, receiverCharacteristic, broadcasterCharacteristic).apply(Tunnel::connect)
    }

    override fun onDeviceDisconnectedFromServer(device: BluetoothDevice) {
        tunnel?.closeConnection()
        tunnel = null
    }
}