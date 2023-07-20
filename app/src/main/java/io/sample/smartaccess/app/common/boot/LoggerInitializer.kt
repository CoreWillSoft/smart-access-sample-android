package io.sample.smartaccess.app.common.boot

import timber.log.Timber

class LoggerInitializer(enableLogger: Boolean) {
    init {
        if (enableLogger) Timber.plant(Timber.DebugTree())
    }
}
