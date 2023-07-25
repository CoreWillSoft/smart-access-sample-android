package io.sample.smartaccess.app.common.di

import io.sample.smartaccess.app.BuildConfig
import io.sample.smartaccess.app.SmartAccessApp
import io.sample.smartaccess.app.common.boot.LoggerInitializer
import io.sample.smartaccess.app.feature.map.geofenceUiModule
import io.sample.smartaccess.data.ble.bleDataModule
import io.sample.smartaccess.data.geofense.geofenceDataModule
import io.sample.smartaccess.domain.geofenceDomainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun SmartAccessApp.attachDi() {
    startKoin(appDeclaration = diDeclaration())
}

internal fun SmartAccessApp.diDeclaration(): KoinAppDeclaration = {
    androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
    androidContext(this@diDeclaration)
    modules(
        /*region Root Modules*/
        startupModule,
        coroutinesModule,
        geofenceUiModule,
        geofenceDataModule,
        geofenceDomainModule,
        bleDataModule
    )
}

val startupModule = module(createdAtStart = true) {
    single { LoggerInitializer(BuildConfig.DEBUG) }
}
