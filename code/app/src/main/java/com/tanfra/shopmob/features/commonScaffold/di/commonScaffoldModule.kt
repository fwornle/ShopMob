package com.tanfra.shopmob.features.commonScaffold.di

import com.tanfra.shopmob.features.common.monitor.ConnectivityMonitorImpl
import com.tanfra.shopmob.features.commonScaffold.presentation.ScaffoldDefaultActionProcessor
import com.tanfra.shopmob.features.commonScaffold.presentation.ScaffoldDefaultMutationReducer
import com.tanfra.shopmob.features.commonScaffold.presentation.ScaffoldViewModelMvi
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val commonScaffoldModule = module {
    viewModel {
        ScaffoldViewModelMvi(
            actionProcessors = listOf(
                ScaffoldDefaultActionProcessor(
                    connectivityMonitor = ConnectivityMonitorImpl(
                        context = androidContext(),
                    )
                ),
            ),
            reducers = listOf(
                ScaffoldDefaultMutationReducer(
                    resources = androidContext().resources,
                ),
            ),
            dispatcherProvider = get(),
        )
    }
}