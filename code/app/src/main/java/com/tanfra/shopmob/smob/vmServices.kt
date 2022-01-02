package com.tanfra.shopmob.smob

import com.tanfra.shopmob.smob.data.repo.dataSource.SmobItemDataSource
import com.tanfra.shopmob.smob.activities.planning.saveitem.SaveSmobItemViewModel
import com.tanfra.shopmob.smob.activities.planning.smoblist.SmobItemListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// Koin module for viewModel (services)
val vmServices = module {

    // declare a ViewModel - to be injected into Fragment with dedicated injector using
    // "by viewModel()"
    //
    // class SmobItemListViewModel(
    //    app: Application,
    //    private val dataSource: SmobItemDataSource
    // ) : BaseViewModel(app) { ... }
    viewModel {
        SmobItemListViewModel(
            get(),  // app (context)
            get() as SmobItemDataSource  // repo as data source
        )
    }

    // declare a ViewModel - to be injected into Fragment with standard injector using
    // "by inject()"
    // --> this view model is declared singleton to be used across multiple fragments
    //
    // class SaveSmobItemViewModel(
    //    val app: Application,
    //    val dataSource: SmobItemDataSource
    // ) : BaseViewModel(app) { ... }
    single {
        SaveSmobItemViewModel(
            get(),
            get() as SmobItemDataSource
        )
    }

}  // viewModelModule