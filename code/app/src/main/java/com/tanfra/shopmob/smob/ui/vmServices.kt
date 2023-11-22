package com.tanfra.shopmob.smob.ui

import com.tanfra.shopmob.features.common.arch.ActionProcessor
import com.tanfra.shopmob.features.common.arch.Reducer
import com.tanfra.shopmob.features.common.dispatcher.DispatcherProvider
import com.tanfra.shopmob.features.commonScaffold.presentation.ScaffoldViewModelMvi
import com.tanfra.shopmob.features.commonScaffold.presentation.model.ScaffoldAction
import com.tanfra.shopmob.features.commonScaffold.presentation.model.ScaffoldEvent
import com.tanfra.shopmob.features.commonScaffold.presentation.model.ScaffoldMutation
import com.tanfra.shopmob.features.commonScaffold.presentation.view.ScaffoldViewState
import com.tanfra.shopmob.features.smobPlanning.presentation.PlanningViewModelMvi
import com.tanfra.shopmob.smob.data.repo.repoIf.*
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import com.tanfra.shopmob.smob.ui.details.SmobDetailsViewModel
import com.tanfra.shopmob.features.smobPlanning.presentation.obsoleteRemove.PlanningViewModel
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningAction
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningEvent
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningMutation
import com.tanfra.shopmob.features.smobPlanning.presentation.view.PlanningViewState
import com.tanfra.shopmob.features.smobPlanning.presentation.view.shops.addNewItem.PlanningShopsAddNewItemViewModel
import com.tanfra.shopmob.smob.ui.shopping.SmobShoppingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// Koin module for viewModel (services)
val vmServices = module {

    // planning view models ---------------------------------------------------

    viewModel {
        ScaffoldViewModelMvi(
            actionProcessors = get() as Collection< ActionProcessor<ScaffoldAction, ScaffoldMutation, ScaffoldEvent>>,
            reducers = get() as Collection<Reducer<ScaffoldMutation, ScaffoldViewState>>,
            dispatcherProvider = get() as DispatcherProvider,
            initialState = get() as ScaffoldViewState,
        )
    }

    viewModel {
        PlanningViewModelMvi(
            actionProcessors = get() as Collection< ActionProcessor<PlanningAction, PlanningMutation, PlanningEvent>>,
            reducers = get() as Collection<Reducer<PlanningMutation, PlanningViewState>>,
            dispatcherProvider = get() as DispatcherProvider,
            initialState = get() as PlanningViewState,
        )
    }

    // ProductList fragment & ProductEdit & ShopEdit
    viewModel {
        PlanningViewModel(
            app = get(),
            listRepository = get() as SmobListRepository,  // repo as data source
            productRepository = get() as SmobProductRepository,  // repo as data source
            shopRepository = get() as SmobShopRepository,  // repo as data source
            groupRepository = get() as SmobGroupRepository,  // repo as data source
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