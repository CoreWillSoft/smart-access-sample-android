package io.sample.smartaccess.app.common.di

import io.sample.smartaccess.app.common.security.SecurityProvider
import io.sample.smartaccess.app.common.security.SecurityProviderImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val securityModule = module {
    factory<SecurityProvider> { SecurityProviderImpl(androidApplication()) }
}
