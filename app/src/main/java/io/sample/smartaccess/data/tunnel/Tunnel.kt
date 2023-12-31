package io.sample.smartaccess.data.tunnel

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import io.sample.smartaccess.data.ble.communication.BleCommunication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.BleServerManager
import no.nordicsemi.android.ble.callback.DataReceivedCallback
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.ktx.suspend
import org.koin.core.context.GlobalContext
import timber.log.Timber

internal interface Tunnel {

    fun connect()

    fun closeConnection()

    companion object {
        fun make(
            device: BluetoothDevice,
            server: BleServerManager,
            context: Context,
            receiverCharacteristic: BluetoothGattCharacteristic,
            broadcasterCharacteristic: BluetoothGattCharacteristic,
        ): Tunnel = SimpleTunnel(device, receiverCharacteristic, broadcasterCharacteristic, server, context)
    }
}

private class SimpleTunnel(
    private val device: BluetoothDevice,
    private val receiverCharacteristic: BluetoothGattCharacteristic,
    private val broadcasterCharacteristic: BluetoothGattCharacteristic,
    private val server: BleServerManager,
    context: Context
) : Tunnel, BleManager(context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val incomingFlow by lazy {
        callbackFlow {
            val callback = DataReceivedCallback { _, data ->
                trySendBlocking(data)
            }
            waitForWrite(receiverCharacteristic).with(callback).enqueue()
            awaitClose {}
        }
    }

    init {
        useServer(server)
    }

    override fun connect() {
        connect(device).enqueue()
        incomingFlow.onEach(::processCommunication)
            .catch { error -> log(Log.ERROR, error.message ?: error.localizedMessage) }.launchIn(scope = scope)
    }

    override fun closeConnection() {
        scope.cancel()
        close()
    }

    override fun log(priority: Int, message: String) {
        Timber.log(priority, message)
    }

    override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
        return true
    }

    private suspend fun processCommunication(data: Data) {
        val communication = GlobalContext.get().get<BleCommunication>().invoke(data)
        sendNotification(broadcasterCharacteristic, communication()).suspend()
    }
}