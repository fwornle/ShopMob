package com.tanfra.shopmob.features.smobAdmin.di

import com.tanfra.shopmob.features.common.monitor.ConnectivityMonitorImpl
import com.tanfra.shopmob.features.smobAdmin.presentation.AdminViewModelMvi
import com.tanfra.shopmob.features.smobAdmin.presentation.AdminActionProcessor
import com.tanfra.shopmob.features.smobAdmin.presentation.AdminMutationReducer
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val smobAdminModule = module {
    viewModel {
        AdminViewModelMvi(
            actionProcessors = listOf(
                AdminActionProcessor(
                    context = androidContext(),
                    listRepository = get(),
                    groupRepository = get(),
                    connectivityMonitor = ConnectivityMonitorImpl(
                        context = androidContext(),
                    )
                ),
            ),
            reducers = listOf(
                AdminMutationReducer(
                    resources = androidContext().resources,
                ),
            ),
            dispatcherProvider = get(),
        )
    }
}