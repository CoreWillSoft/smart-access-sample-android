package io.sample.smartaccess.data.ble

import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings

interface GattAdvertiseFactory {

    fun settings(): AdvertiseSettings

    fun data(): AdvertiseData
}

internal class GattAdvertiseFactoryImpl : GattAdvertiseFactory {

    override fun settings(): AdvertiseSettings = AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
        .setConnectable(true)
        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
        .build()

    override fun data(): AdvertiseData = AdvertiseData.Builder()
        .setIncludeDeviceName(true)
        .addManufacturerData(0x004C, byteArrayOf())
//            .addServiceUuid(ParcelUuid(SERVICE_UUID))
        .build()
}

