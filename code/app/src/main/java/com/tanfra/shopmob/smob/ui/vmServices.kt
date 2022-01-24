package com.tanfra.shopmob.smob.ui

import com.tanfra.shopmob.smob.ui.planning.lists.PlanningListsViewModel
import com.tanfra.shopmob.smob.ui.planning.productList.PlanningProductListViewModel
import com.tanfra.shopmob.smob.ui.planning.shopEdit.PlanningShopEditViewModel
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobListDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// Koin module for viewModel (services)
val vmServices = module {

    // planning view models ---------------------------------------------------

    // Lists fragment
    viewModel {
        PlanningListsViewModel(
            get(),  // app (context)
            get() as SmobListDataSource,  // repo as data source
        )
    }

    // ProductList fragment & ProductEdit & ShopEdit
    viewModel {
        PlanningProductListViewModel(
            get(),  // app (context)
            get() as SmobListDataSource,  // repo as data source
            get() as SmobProductDataSource,  // repo as data source
            get() as SmobShopDataSource,  // repo as data source
        )
    }

    // ShopEdit fragment
    viewModel {
        PlanningShopEditViewModel(
            get(),  // app (context)
            get() as SmobShopDataSource,  // repo as data source
        )
    }


}  // viewModelModule