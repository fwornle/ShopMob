package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.repo.dataSource.*
import org.koin.dsl.module

// Koin module for repository services - abstracts DB and NET
val repoServices = module {

    // DataSources ------------------------------------------------------------

    // declare a (singleton) repository service with interface "SmobItemDataSource"
    // ... the repo requires two constructor provided dependencies: dao & api
    single<SmobItemDataSource> { SmobItemRepository(get()) }

    // declare a (singleton) repository service with interface "SmobUserDataSource"
    // ... the repo requires two constructor provided dependencies: dao & api
    single<SmobUserDataSource> { SmobUserRepository(get(), get()) }

    // declare a (singleton) repository service with interface "SmobGroupDataSource"
    // ... the repo requires two constructor provided dependencies: dao & api
    single<SmobGroupDataSource> { SmobGroupRepository(get()) }

    // declare a (singleton) repository service with interface "SmobShopDataSource"
    // ... the repo requires two constructor provided dependencies: dao & api
    single<SmobShopDataSource> { SmobShopRepository(get()) }

    // declare a (singleton) repository service with interface "SmobProductDataSource"
    // ... the repo requires two constructor provided dependencies: dao & api
    single<SmobProductDataSource> { SmobProductRepository(get()) }

}  // repoServices