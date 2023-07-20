package io.sample.smartaccess.app.common.di

import io.sample.smartaccess.app.common.coroutine.createApplicationScope
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val APP_COROUTINE_SCOPE = "APP_COROUTINE_SCOPE"

val coroutinesModule = module {
    single(qualifier = named(APP_COROUTINE_SCOPE)) { createApplicationScope() }
}
