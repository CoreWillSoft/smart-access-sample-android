package io.sample.smartaccess.app.common.di

import io.sample.smartaccess.app.BuildConfig
import io.sample.smartaccess.app.TemplateApp
import io.sample.smartaccess.app.common.boot.LoggerInitializer
import io.sample.smartaccess.app.feature.map.geofenceUiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun TemplateApp.attachDi() {
    startKoin(appDeclaration = diDeclaration())
}

internal fun TemplateApp.diDeclaration(): KoinAppDeclaration = {
    androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
    androidContext(this@diDeclaration)
    modules(
        /*region Root Modules*/
        startupModule,
        coroutinesModule,
        geofenceUiModule,
        /*endregion*/
        /*region IO*/
        securityModule,
        serializerModule,
        /*endregion*/
        /*region Core Domain*/

        /*endregion*/
    )
}

val startupModule = module(createdAtStart = true) {
    single { LoggerInitializer(BuildConfig.DEBUG) }
}
