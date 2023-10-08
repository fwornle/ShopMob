package com.tanfra.shopmob.features.common

import com.tanfra.shopmob.features.common.dispatcher.DispatcherProvider
import com.tanfra.shopmob.features.common.dispatcher.StandardDispatcherProvider
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val commonModule = module {
    singleOf(::StandardDispatcherProvider) { bind<DispatcherProvider>() }
}