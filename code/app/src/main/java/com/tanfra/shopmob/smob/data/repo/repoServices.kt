package com.tanfra.shopmob.smob.data.repo

import com.tanfra.shopmob.smob.data.repo.dataSource.SmobGroupDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobListDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobUserDataSource
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
//    single<SmobItemDataSource<SmobUserATO>> {
//        SmobItemRepository<SmobUserDTO, SmobUserNTO, SmobUserATO>(
//            get() /* SmobItemDao<SmobUserDTO> */,
//            get() /* SmobItemApi<SmobUserNTO> */,
//            dummySmobUserDTO
//        )
//    }

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


//package com.tanfra.shopmob.smob.data.repo
//
//import com.tanfra.shopmob.smob.data.local.dao.SmobItemDao
//import com.tanfra.shopmob.smob.data.local.dao.SmobUserDao
//import com.tanfra.shopmob.smob.data.local.dao.SmobUserDao_Impl
//import com.tanfra.shopmob.smob.data.local.dto.SmobGroupDTO
//import com.tanfra.shopmob.smob.data.local.dto.SmobListDTO
//import com.tanfra.shopmob.smob.data.local.dto.SmobProductDTO
//import com.tanfra.shopmob.smob.data.local.dto.SmobShopDTO
//import com.tanfra.shopmob.smob.data.local.dto.SmobUserDTO
//import com.tanfra.shopmob.smob.data.net.nto.SmobGroupNTO
//import com.tanfra.shopmob.smob.data.net.nto.SmobListNTO
//import com.tanfra.shopmob.smob.data.net.nto.SmobProductNTO
//import com.tanfra.shopmob.smob.data.net.nto.SmobShopNTO
//import com.tanfra.shopmob.smob.data.net.nto.SmobUserNTO
//import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
//import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
//import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
//import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
//import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
//import com.tanfra.shopmob.smob.data.repo.dataSource.SmobGroupDataSource
//import com.tanfra.shopmob.smob.data.repo.dataSource.SmobItemDataSource
//import com.tanfra.shopmob.smob.data.repo.dataSource.SmobListDataSource
//import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductDataSource
//import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource
//import com.tanfra.shopmob.smob.data.repo.dataSource.SmobUserDataSource
//import org.koin.dsl.module
//
//// Koin module for repository services - abstracts DB and NET
//val repoServices = module {
//
//    // dummy types - needed to communicate data type to extension function
//    // 'asDomainType' (Kotlin/Java --> type erasure --> no reflection possible)
//    val dummySmobUserDTO = SmobUserDTO()
//    val dummySmobGroupDTO = SmobGroupDTO()
//    val dummySmobProductDTO = SmobProductDTO()
//    val dummySmobShopDTO = SmobShopDTO()
//    val dummySmobListDTO = SmobListDTO()
//
//    // DataSources ------------------------------------------------------------
//
//    // declare a (singleton) repository service with interface "SmobUserDataSource"
//    single<SmobItemDataSource<SmobUserATO>> {
//        SmobItemRepositoryImpl<SmobUserDTO, SmobUserNTO, SmobUserATO>(
//            get() /* SmobItemDao<SmobUserDTO> */,
//            get() /* SmobItemApi<SmobUserNTO> */,
//            dummySmobUserDTO
//        )
//    }
//    single<SmobUserDataSource> {
//        SmobUserRepository(
//            get() /* SmobUserDao */,
//            get() /* SmobItemDataSource<SmobUserATO>> */
//        ) }
//
//    // declare a (singleton) repository service with interface "SmobGroupDataSource"
//    single<SmobItemDataSource<SmobGroupATO>> {
//        SmobItemRepositoryImpl<SmobGroupDTO, SmobGroupNTO, SmobGroupATO>(
//            get() /* SmobItemDao<SmobGroupDTO> */,
//            get() /* SmobItemApi<SmobGroupNTO> */,
//            dummySmobGroupDTO
//        )
//    }
//    single<SmobGroupDataSource> {
//        SmobGroupRepository(
//            get() /* SmobGroupDao */,
//            get() /* SmobItemDataSource<SmobGroupATO>> */
//        )
//    }
//
//    // declare a (singleton) repository service with interface "SmobShopDataSource"
//    single<SmobItemDataSource<SmobShopATO>> {
//        SmobItemRepositoryImpl<SmobShopDTO, SmobShopNTO, SmobShopATO>(
//            get() /* SmobItemDao<SmobShopDTO> */,
//            get() /* SmobItemApi<SmobShopNTO> */,
//            dummySmobShopDTO
//        )
//    }
//    single<SmobShopDataSource> {
//        SmobShopRepository(
//            get() /* SmobShopDao */,
//        )
//    }
//
//    // declare a (singleton) repository service with interface "SmobProductDataSource"
//    single<SmobItemDataSource<SmobProductATO>> {
//        SmobItemRepositoryImpl<SmobProductDTO, SmobProductNTO, SmobProductATO>(
//            get() /* SmobItemDao<SmobProductDTO> */,
//            get() /* SmobItemApi<SmobProductNTO> */,
//            dummySmobProductDTO
//        )
//    }
//    single<SmobProductDataSource> {
//        SmobProductRepository(
//            get() /* SmobProductDao */,
//            get() /* SmobItemDataSource<SmobProductATO>> */
//        )
//    }
//
//    // declare a (singleton) repository service with interface "SmobListDataSource"
//    single<SmobItemDataSource<SmobListATO>> {
//        SmobItemRepositoryImpl<SmobListDTO, SmobListNTO, SmobListATO>(
//            get() /* SmobItemDao<SmobListDTO> */,
//            get() /* SmobItemApi<SmobListNTO> */,
//            dummySmobListDTO
//        )
//    }
//    single<SmobListDataSource> {
//        SmobListRepository(
//            get() /* SmobItemDataSource<SmobListATO>> */
//        )
//    }
//
//}  // repoServices