package com.tanfra.shopmob.smob.domain.work

import org.koin.dsl.module

// Koin module for WorkManager (services)
val wmServices = module {

    // create singleton of WorkManager configuration class
    single { SmobAppWork(get()) }

}  // WorkManager services