package com.tanfra.shopmob.features.smobPlanning.di

import com.tanfra.shopmob.features.common.monitor.ConnectivityMonitorImpl
import com.tanfra.shopmob.features.smobPlanning.presentation.PlanningViewModelMvi
import com.tanfra.shopmob.features.smobPlanning.presentation.DefaultActionProcessor
import com.tanfra.shopmob.features.smobPlanning.presentation.DefaultReducer
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.SmobListsActionProcessor
import com.tanfra.shopmob.features.smobPlanning.presentation.view.products.SmobProductsActionProcessor
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.SmobListsReducer
import com.tanfra.shopmob.features.smobPlanning.presentation.view.products.SmobProductsReducer
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val smobPlanningModule = module {
    viewModel {
        PlanningViewModelMvi(
            actionProcessors = listOf(
                DefaultActionProcessor(
                    context = androidContext(),
                    listRepository = get(),
                    productRepository = get(),
                    groupRepository = get(),
                    connectivityMonitor = ConnectivityMonitorImpl(
                        context = androidContext(),
                    )
                ),
                SmobListsActionProcessor(
                    listRepository = get(),
                ),
                SmobProductsActionProcessor(
                    listRepository = get(),
                    productRepository = get(),
                ),
            ),
            reducers = listOf(
                DefaultReducer(
                    resources = androidContext().resources,
                ),
                SmobListsReducer(),
                SmobProductsReducer(),
            ),
            dispatcherProvider = get(),
        )
    }
}