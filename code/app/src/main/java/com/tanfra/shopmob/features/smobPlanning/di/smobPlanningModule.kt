package com.tanfra.shopmob.features.smobPlanning.di

import com.tanfra.shopmob.features.common.monitor.ConnectivityMonitorImpl
import com.tanfra.shopmob.features.smobPlanning.presentation.PlanningViewModelMvi
import com.tanfra.shopmob.features.smobPlanning.presentation.PlanningActionProcessor
import com.tanfra.shopmob.features.smobPlanning.presentation.PlanningMutationReducer
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.PlanningListsActionProcessor
import com.tanfra.shopmob.features.smobPlanning.presentation.view.products.PlanningProductsActionProcessor
import com.tanfra.shopmob.features.smobPlanning.presentation.view.lists.PlanningListsMutationReducer
import com.tanfra.shopmob.features.smobPlanning.presentation.view.products.PlanningProductsMutationReducer
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val smobPlanningModule = module {
    viewModel {
        PlanningViewModelMvi(
            actionProcessors = listOf(
                PlanningActionProcessor(
                    context = androidContext(),
                    listRepository = get(),
                    productRepository = get(),
                    groupRepository = get(),
                    connectivityMonitor = ConnectivityMonitorImpl(
                        context = androidContext(),
                    )
                ),
                PlanningListsActionProcessor(
                    listRepository = get(),
                ),
                PlanningProductsActionProcessor(
                    listRepository = get(),
                    productRepository = get(),
                ),
            ),
            reducers = listOf(
                PlanningMutationReducer(
                    resources = androidContext().resources,
                ),
                PlanningListsMutationReducer(),
                PlanningProductsMutationReducer(),
            ),
            dispatcherProvider = get(),
        )
    }
}