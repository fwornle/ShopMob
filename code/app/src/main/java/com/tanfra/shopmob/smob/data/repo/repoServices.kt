package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.local.dao.SmobItemDao
import com.tanfra.shopmob.smob.data.local.dto.*
import com.tanfra.shopmob.smob.data.net.nto.*
import com.tanfra.shopmob.smob.data.repo.ato.*
import com.tanfra.shopmob.smob.data.repo.dataSource.*
import org.koin.dsl.module

// Koin module for repository services - abstracts DB and NET
val repoServices = module {

//    // dummy types
//    val dummySmobUserDTO = SmobUserDTO()
//    val dummySmobGroupDTO = SmobGroupDTO()
//    val dummySmobProductDTO = SmobProductDTO()
//    val dummySmobShopDTO = SmobShopDTO()
//    val dummySmobListDTO = SmobListDTO()

    // DataSources ------------------------------------------------------------

    // declare a (singleton) repository service with interface "SmobUserDataSource"
    // ... the repo requires two constructor provided dependencies: dao & api
    single<SmobUserDataSource> { SmobUserRepository(get(), get()) }
    //single { SmobItemRepository<SmobUserDTO, SmobUserNTO, SmobUserATO>(get() as SmobItemDao<SmobUserDTO>, get(), dummySmobUserDTO) as SmobUserDataSource }

    // declare a (singleton) repository service with interface "SmobGroupDataSource"
    // ... the repo requires two constructor provided dependencies: dao & api
    single<SmobGroupDataSource> { SmobGroupRepository(get(), get()) }
    //single { SmobItemRepository<SmobGroupDTO, SmobGroupNTO, SmobGroupATO>(get() as SmobItemDao<SmobGroupDTO>, get(), dummySmobGroupDTO) as SmobGroupDataSource }

    // declare a (singleton) repository service with interface "SmobShopDataSource"
    // ... the repo requires two constructor provided dependencies: dao & api
    single<SmobShopDataSource> { SmobShopRepository(get(), get()) }
    //single { SmobItemRepository<SmobShopDTO, SmobShopNTO, SmobShopATO>(get() as SmobItemDao<SmobShopDTO>, get(), dummySmobShopDTO) as SmobShopDataSource }

    // declare a (singleton) repository service with interface "SmobProductDataSource"
    // ... the repo requires two constructor provided dependencies: dao & api
    single<SmobProductDataSource> { SmobProductRepository(get(), get()) }
    //single { SmobItemRepository<SmobProductDTO, SmobProductNTO, SmobProductATO>(get() as SmobItemDao<SmobProductDTO>, get(), dummySmobProductDTO) as SmobProductDataSource }

    // declare a (singleton) repository service with interface "SmobListDataSource"
    // ... the repo requires two constructor provided dependencies: dao & api
    single<SmobListDataSource> { SmobListRepository(get(), get()) }
    //single { SmobItemRepository<SmobListDTO, SmobListNTO, SmobListATO>(get() as SmobItemDao<SmobListDTO>, get(), dummySmobListDTO) as SmobListDataSource }

}  // repoServices