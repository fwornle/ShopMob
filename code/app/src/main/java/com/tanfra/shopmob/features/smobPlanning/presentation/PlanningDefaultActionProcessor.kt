package com.tanfra.shopmob.features.smobPlanning.presentation

import android.content.Context
import android.os.Vibrator
import com.tanfra.shopmob.features.common.arch.ActionProcessor
import com.tanfra.shopmob.features.common.monitor.ConnectivityMonitor
import com.tanfra.shopmob.features.common.monitor.ConnectivityStatus
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Action
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Event
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Mutation
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobGroupRepository
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobListRepository
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobProductRepository
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.ui.zeUtils.vibrateDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class PlanningDefaultActionProcessor(
    private val context: Context,
    private val listRepository: SmobListRepository,
    private val productRepository: SmobProductRepository,
    private val groupRepository: SmobGroupRepository,
    private val connectivityMonitor: ConnectivityMonitor,
) : ActionProcessor<Action, Mutation, Event> {

    override fun invoke(action: Action): Flow<Pair<Mutation?, Event?>> =
        if (action is Action.CheckConnectivity) {
            checkConnectivity()
        } else flow {
            when (action) {
                Action.LoadLists -> loadLists()
                Action.LoadGroups -> loadGroups()
                Action.RefreshLists -> refreshLists()
                Action.RefreshProducts -> refreshProducts()
                Action.IllegalSwipe -> illegalSwipeAction()
                else -> {
                    //no-op
                }
            }
        }


    // Actions ---------------------------------------------------------------------------
    // Actions ---------------------------------------------------------------------------
    // Actions ---------------------------------------------------------------------------

    private fun checkConnectivity() =
        connectivityMonitor.statusFlow
            .map { status ->
                if (status == ConnectivityStatus.AVAILABLE) {
                    Mutation.DismissLostConnection
                } else {
                    Mutation.ShowLostConnection
                } to null
            }


    // load lists from local DB
    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.loadLists() {
        emit(Mutation.ShowLoader to null)

        listRepository.getSmobItems().collect {
            when(it) {
                Resource.Empty -> {
                    Timber.i("list flow collection returns empty")
                    emit(Mutation.ShowLists(lists = listOf()) to null)
                }
                is Resource.Failure -> {
                    Timber.i("list flow collection returns error")
                    emit(Mutation.ShowError(exception = it.exception) to null)
                }
                is Resource.Success -> {
                    Timber.i("list flow collection successful")
                    emit(Mutation.ShowLists(lists = it.data) to null)
                }
            }
        }
    }


    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.loadGroups() {
        groupRepository.getSmobItems().collect {
            when(it) {
                Resource.Empty -> {
                    Timber.i("group flow collection returns empty")
                    emit(Mutation.ShowFormWithGroups(groups = listOf()) to null)
                }
                is Resource.Failure -> {
                    Timber.i("group flow collection returns error")
                    emit(Mutation.ShowError(exception = it.exception) to null)
                }
                is Resource.Success -> {
                    Timber.i("group flow collection successful")
                    emit(Mutation.ShowFormWithGroups(groups = it.data) to null)
                }
            }
        }
    }


    // refreshing view (= load lists from backend)
    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.refreshLists() {
        emit(null to Event.Refreshing(true))

        // update local DB from backend DB (via net API)
        listRepository.refreshItemsInLocalDB()

        emit(null to Event.Refreshing(false))
    }


    // refreshing view (= load products from backend)
    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.refreshProducts() {
        emit(null to Event.Refreshing(true))

        // update local DB from backend DB (via net API)
        productRepository.refreshItemsInLocalDB()

        emit(null to Event.Refreshing(false))
    }


    // illegal swipe transition --> vibrate phone
    private fun illegalSwipeAction() {
        val vib = context.getSystemService(Vibrator::class.java)
        vibrateDevice(vib, 150)
    }

}

