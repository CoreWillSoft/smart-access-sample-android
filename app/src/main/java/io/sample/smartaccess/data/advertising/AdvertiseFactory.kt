package io.sample.smartaccess.data.advertising

import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings

interface AdvertiseFactory {

    fun makeSettings(): AdvertiseSettings

    fun makeData(): AdvertiseData
}

internal class AdvertiseFactoryImpl : AdvertiseFactory {

    override fun makeSettings(): AdvertiseSettings = AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
        .setConnectable(true)
        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
        .build()

    override fun makeData(): AdvertiseData = AdvertiseData.Builder()
        .setIncludeDeviceName(true)
        .addManufacturerData(0x004C, byteArrayOf())
//            .addServiceUuid(ParcelUuid(SERVICE_UUID))
        .build()
}

