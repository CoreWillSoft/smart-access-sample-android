package io.sample.smartaccess.app

import android.app.Application
import io.sample.smartaccess.app.common.di.attachDi

open class SmartAccessApp : Application() {

    internal open val attachDiOnStart = true

    override fun onCreate() {
        super.onCreate()
        if (attachDiOnStart) attachDi()
    }
}
