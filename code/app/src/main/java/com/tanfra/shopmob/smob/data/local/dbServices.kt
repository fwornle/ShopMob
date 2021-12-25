package com.tanfra.shopmob.smob.data.local.dto

import com.tanfra.shopmob.smob.data.local.LocalDB
import com.tanfra.shopmob.smob.data.repo.*
import com.tanfra.shopmob.smob.data.repo.dataSource.*
import org.koin.dsl.module

// Koin module for (local) DB services
val dbServices = module {

    // Room DB ----------------------------------------------------------------

    // local DB singleton "Room" object representing smob database
    // ... used as (local) data source for all repositories of the app
    // ... application context via 'get()'
    single { LocalDB.createSmobDatabase(get()) }

    // DAOs -------------------------------------------------------------------

    // DAO to access table smobItems in the above DB (smob.db)
    single { LocalDB.createSmobItemDao(get()) }

    // DAO to access table smobUsers in the above DB (smob.db)
    single { LocalDB.createSmobUserDao(get()) }

    // DAO to access table smobGroups in the above DB (smob.db)
    single { LocalDB.createSmobGroupDao(get()) }

    // DAO to access table smobShops in the above DB (smob.db)
    single { LocalDB.createSmobShopDao(get()) }

    // DAO to access table smobProducts in the above DB (smob.db)
    single { LocalDB.createSmobProductDao(get()) }

}  // dbServices