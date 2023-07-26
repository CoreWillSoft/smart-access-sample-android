package io.sample.smartaccess.data.ble

import io.sample.smartaccess.data.ble.communication.BleCommunication
import io.sample.smartaccess.data.ble.communication.SimpleBleCommunication
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val bleDataModule = module {
    singleOf<GattAdvertiseFactory>(::GattAdvertiseFactoryImpl)
    factoryOf<BleCommunication>(::SimpleBleCommunication)
}