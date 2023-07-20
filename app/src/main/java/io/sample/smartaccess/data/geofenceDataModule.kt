package io.sample.smartaccess.data

import io.sample.smartaccess.data.advertising.AdvertiseFactory
import io.sample.smartaccess.data.advertising.AdvertiseFactoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val geofenceDataModule = module {
    single { GeofenceManager(get()) }
    singleOf<AdvertiseFactory>(::AdvertiseFactoryImpl)
}