package com.tanfra.shopmob.features.smobPlanning.di

import com.tanfra.shopmob.features.common.monitor.ConnectivityMonitorImpl
import com.tanfra.shopmob.features.smobPlanning.presentation.PlanningViewModelMvi
import com.tanfra.shopmob.features.smobPlanning.presentation.ActionProcessorDefault
import com.tanfra.shopmob.features.smobPlanning.presentation.DefaultReducer
import com.tanfra.shopmob.features.smobPlanning.presentation.ActionProcessorSmobLists
import com.tanfra.shopmob.features.smobPlanning.presentation.UserActionReducer
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val smobPlanningModule = module {
    viewModel {
        PlanningViewModelMvi(
            actionProcessors = listOf(
                ActionProcessorDefault(
                    listRepository = get(),
                    connectivityMonitor = ConnectivityMonitorImpl(
                        context = androidContext(),
                    )
                ),
                ActionProcessorSmobLists(
                    context = androidContext(),
                    listRepository = get(),
                    productRepository = get(),
                    groupRepository = get()
                ),
            ),
            reducers = listOf(
                DefaultReducer(
                    resources = androidContext().resources,
                ),
                UserActionReducer()
            ),
            dispatcherProvider = get(),
        )
    }
}