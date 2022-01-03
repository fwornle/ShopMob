package com.tanfra.shopmob.smob

import com.tanfra.shopmob.smob.activities.administration.AdminViewModel
import com.tanfra.shopmob.smob.activities.planning.lists.PlanningListsViewModel
import com.tanfra.shopmob.smob.activities.planning.productEdit.PlanningProductEditViewModel
import com.tanfra.shopmob.smob.activities.planning.productList.PlanningProductListViewModel
import com.tanfra.shopmob.smob.activities.planning.shopEdit.PlanningShopEditViewModel
import com.tanfra.shopmob.smob.activities.planning.shopList.PlanningShopListViewModel
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
            get() as SmobListDataSource  // repo as data source
        )
    }

    // ProductList fragment
    viewModel {
        PlanningProductListViewModel(
            get(),  // app (context)
            get() as SmobProductDataSource  // repo as data source
        )
    }

    // ProductEdit fragment
    viewModel {
        PlanningProductEditViewModel(
            get(),  // app (context)
            get() as SmobProductDataSource  // repo as data source
        )
    }

    // ShopList fragment
    viewModel {
        PlanningShopListViewModel(
            get(),  // app (context)
            get() as SmobShopDataSource  // repo as data source
        )
    }

    // ShopEdit fragment
    viewModel {
        PlanningShopEditViewModel(
            get(),  // app (context)
            get() as SmobShopDataSource  // repo as data source
        )
    }

    // admin view models ---------------------------------------------------

    // Lists fragment
    viewModel {
        AdminViewModel(
            get(),  // app (context)
            get() as SmobListDataSource  // repo as data source
        )
    }

}  // viewModelModule