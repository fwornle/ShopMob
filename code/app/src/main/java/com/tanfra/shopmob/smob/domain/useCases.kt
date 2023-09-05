package com.tanfra.shopmob.smob.domain

import org.koin.dsl.module

// Koin module for usecases
val useCases = module {

    single { FetchShopDetailsUseCase(get()) }
    single { FetchProductDetailsUseCase(get()) }

}  // usecases