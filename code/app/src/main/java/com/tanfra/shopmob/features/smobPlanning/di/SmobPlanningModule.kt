package com.tanfra.shopmob.features.smobPlanning.di

import com.tanfra.shopmob.features.common.monitor.ConnectivityMonitorImpl
import com.tanfra.shopmob.features.smobPlanning.presentation.PlanningViewModelMvi
import com.tanfra.shopmob.features.smobPlanning.presentation.DefaultActionProcessor
import com.tanfra.shopmob.features.smobPlanning.presentation.DefaultReducer
import com.tanfra.shopmob.features.smobPlanning.presentation.UserActionProcessor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val smobPlanningModule = module {
    viewModel {
        PlanningViewModelMvi(
            actionProcessors = listOf(
                DefaultActionProcessor(
                    listRepository = get(),
                    connectivityMonitor = ConnectivityMonitorImpl(
                        context = androidContext(),
                    )
                ),
                UserActionProcessor(
                    listRepository = get(),
                    groupRepository = get()
                )
            ),
            reducers = listOf(
                DefaultReducer(
                    resources = androidContext().resources,
                )
            ),
            dispatcherProvider = get(),
        )
    }
}