package com.tanfra.shopmob.smob.data.local

import org.koin.dsl.module

// Koin module for (local) DB services
val dbServices = module {

    // Room DB ----------------------------------------------------------------

    // local DB singleton "Room" object representing smob database
    // ... used as (local) data source for all repositories of the app
    // ... application context via 'get()'
    single { LocalDB.createSmobDatabase(get()) }

    // DAOs -------------------------------------------------------------------

    // DAO to access table smobUsers in the above DB (smob.db)
    single { LocalDB.createSmobUserDao(get()) }

    // DAO to access table smobGroups in the above DB (smob.db)
    single { LocalDB.createSmobGroupDao(get()) }

    // DAO to access table smobShops in the above DB (smob.db)
    single { LocalDB.createSmobShopDao(get()) }

    // DAO to access table smobProducts in the above DB (smob.db)
    single { LocalDB.createSmobProductDao(get()) }

    // DAO to access table smobLists in the above DB (smob.db)
    single { LocalDB.createSmobListDao(get()) }

}  // dbServices