package io.sample.smartaccess.data.ble

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val bleDataModule = module {
    singleOf<GattAdvertiseFactory>(::GattAdvertiseFactoryImpl)
}