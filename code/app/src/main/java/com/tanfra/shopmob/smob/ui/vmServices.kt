package com.tanfra.shopmob.smob.ui

import com.tanfra.shopmob.smob.data.repo.dataSource.*
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import com.tanfra.shopmob.smob.ui.details.SmobDetailsViewModel
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import com.tanfra.shopmob.smob.ui.planning.shops.addNewItem.PlanningShopsAddNewItemViewModel
import com.tanfra.shopmob.smob.ui.shopping.SmobShoppingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// Koin module for viewModel (services)
val vmServices = module {

    // planning view models ---------------------------------------------------

    // ProductList fragment & ProductEdit & ShopEdit
    viewModel {
        PlanningViewModel(
            get(),  // app (context)
            get() as SmobListRepository,  // repo as data source
            get() as SmobProductRepository,  // repo as data source
            get() as SmobShopRepository,  // repo as data source
        )
    }

    // ShopEdit fragment
    viewModel {
        PlanningShopsAddNewItemViewModel(
            get(),  // app (context)
            get() as SmobShopRepository,  // repo as data source
        )
    }


    // admin view models ---------------------------------------------------

    // Lists fragment
    viewModel {
        AdminViewModel(
            get(),  // app (context)
            get() as SmobGroupRepository,  // repo as data source
            get() as SmobListRepository,   // repo as data source
            get() as SmobUserRepository,   // repo as data source
        )
    }



    // details view model ----------------------------------------------------

    // shared with all details fragments (and the activity)
    viewModel {
        SmobDetailsViewModel(
            get(),  // app (context)
//            get() as SmobProductDataSource,  // repo as data source
//            get() as SmobShopDataSource,  // repo as data source
        )
    }


    // shopping view model ----------------------------------------------------

    // (currently) shared with all details fragments (and the activity)
    viewModel {
        SmobShoppingViewModel(
            get(),  // app (context)
//            get() as SmobProductDataSource,  // repo as data source
//            get() as SmobShopDataSource,  // repo as data source
        )
    }

}  // viewModelModule