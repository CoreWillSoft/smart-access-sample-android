package io.sample.smartaccess.data.geofense

import io.sample.smartaccess.data.ble.GattAdvertiseFactory
import io.sample.smartaccess.data.ble.GattAdvertiseFactoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val geofenceDataModule = module {
    single { GeofenceManager(get()) }
    singleOf<GattAdvertiseFactory>(::GattAdvertiseFactoryImpl)
}