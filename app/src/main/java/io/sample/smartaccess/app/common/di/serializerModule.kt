package io.sample.smartaccess.app.common.di

import io.sample.smartaccess.app.common.io.createKotlinJson
import org.koin.dsl.module

val serializerModule = module {
    single { createKotlinJson() }
}
