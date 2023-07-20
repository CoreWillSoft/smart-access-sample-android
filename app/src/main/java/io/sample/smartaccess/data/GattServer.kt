package io.sample.smartaccess.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCharacteristic.PERMISSION_READ
import android.bluetooth.BluetoothGattCharacteristic.PERMISSION_WRITE
import android.bluetooth.BluetoothGattCharacteristic.PROPERTY_NOTIFY
import android.bluetooth.BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.util.Log
import io.sample.smartaccess.data.advertising.AdvertiseFactory
import org.koin.core.context.GlobalContext
import timber.log.Timber
import java.util.UUID


// Random UUID for our service known between the client and server to allow communication
internal val SERVICE_UUID = UUID.fromString("3c25c1cf-b62c-4f3b-8205-c5e078b7d61d")
// Same as the service but for the characteristic
internal val CHARACTERISTIC_UUID = UUID.fromString("3c254203-b62c-4f3b-8205-c5e078b7d61d")

object GattServer {

    private lateinit var gattServer: BluetoothGattServer
    private lateinit var bluetoothLeAdvertiser: BluetoothLeAdvertiser

    private val service  by lazy {
        BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY).also {
            it.addCharacteristic(
                BluetoothGattCharacteristic(
                    CHARACTERISTIC_UUID,
                    PROPERTY_NOTIFY or PROPERTY_WRITE_NO_RESPONSE,
                    PERMISSION_READ or PERMISSION_WRITE,
                ),
            )
        }
    }

    private val advertiseCallback = object : AdvertiseCallback() {}

    private val serverCallback = object : BluetoothGattServerCallback() {

        override fun onConnectionStateChange(
            device: BluetoothDevice,
            status: Int,
            newState: Int,
        ) {

        }
    }

    @SuppressLint("MissingPermission")
    fun create(context: Context) {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        gattServer = manager.openGattServer(context, serverCallback).apply { addService(service) }
        bluetoothLeAdvertiser = manager.adapter.bluetoothLeAdvertiser
    }

    @SuppressLint("MissingPermission")
    fun startAdvertising() {
        val advertiseFactory = GlobalContext.get().get<AdvertiseFactory>()
        bluetoothLeAdvertiser.startAdvertising(advertiseFactory.makeSettings(), advertiseFactory.makeData(), advertiseCallback)
    }

    @SuppressLint("MissingPermission")
    fun stopAdvertising() {
        bluetoothLeAdvertiser.stopAdvertising(advertiseCallback)
        gattServer.close()
    }

}