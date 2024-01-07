package com.tanfra.shopmob.features.smobPlanning.presentation

import android.content.Context
import android.os.Vibrator
import com.tanfra.shopmob.features.common.arch.ActionProcessor
import com.tanfra.shopmob.features.common.monitor.ConnectivityMonitor
import com.tanfra.shopmob.features.common.monitor.ConnectivityStatus
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningAction
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningEvent
import com.tanfra.shopmob.features.smobPlanning.presentation.model.PlanningMutation
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobGroupRepository
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobListRepository
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobProductRepository
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobShopRepository
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.ui.zeUtils.vibrateDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class PlanningActionProcessor(
    private val context: Context,
    private val listRepository: SmobListRepository,
    private val shopRepository: SmobShopRepository,
    private val productRepository: SmobProductRepository,
    private val groupRepository: SmobGroupRepository,
    private val connectivityMonitor: ConnectivityMonitor,
) : ActionProcessor<PlanningAction, PlanningMutation, PlanningEvent> {

    override fun invoke(action: PlanningAction): Flow<Pair<PlanningMutation?, PlanningEvent?>> =
        if (action is PlanningAction.CheckConnectivity) {
            checkConnectivity()
        } else flow {
            when (action) {
                PlanningAction.LoadLists -> loadLists()
                PlanningAction.LoadShops -> loadShops()
                PlanningAction.LoadGroups -> loadGroups()
                PlanningAction.RefreshLists -> refreshLists()
                PlanningAction.RefreshShops -> refreshShops()
                PlanningAction.RefreshProducts -> refreshProducts()
                PlanningAction.IllegalSwipe -> illegalSwipeAction()
                else -> {
                    //no-op
                    Timber.i("MVI.UI: ${action.toString().take(50)}... not found in PlanningActionProcessor")
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
                    PlanningMutation.DismissLostConnection
                } else {
                    PlanningMutation.ShowLostConnection
                } to null
            }


    // load lists from local DB
    private suspend fun FlowCollector<Pair<PlanningMutation?, PlanningEvent?>>.loadLists() {
        emit(PlanningMutation.ShowLoader to null)

        listRepository.getSmobItems().collect {
            when(it) {
                Resource.Empty -> {
                    Timber.i("list flow collection returns empty")
                    emit(PlanningMutation.ShowLists(lists = listOf()) to null)
                }
                is Resource.Failure -> {
                    Timber.i("list flow collection returns error")
                    emit(PlanningMutation.ShowError(exception = it.exception) to null)
                }
                is Resource.Success -> {
                    Timber.i("list flow collection successful")
                    emit(PlanningMutation.ShowLists(lists = it.data) to null)
                }
            }
        }
    }


    // load lists from local DB
    private suspend fun FlowCollector<Pair<PlanningMutation?, PlanningEvent?>>.loadShops() {
        emit(PlanningMutation.ShowLoader to null)

        shopRepository.getSmobItems().collect {
            when(it) {
                Resource.Empty -> {
                    Timber.i("shop flow collection returns empty")
                    emit(PlanningMutation.ShowShops(shops = listOf()) to null)
                }
                is Resource.Failure -> {
                    Timber.i("shop flow collection returns error")
                    emit(PlanningMutation.ShowError(exception = it.exception) to null)
                }
                is Resource.Success -> {
                    Timber.i("shop flow collection successful")
                    emit(PlanningMutation.ShowShops(shops = it.data) to null)
                }
            }
        }
    }


    private suspend fun FlowCollector<Pair<PlanningMutation?, PlanningEvent?>>.loadGroups() {
        groupRepository.getSmobItems().collect {
            when(it) {
                Resource.Empty -> {
                    Timber.i("group flow collection returns empty")
                    emit(PlanningMutation.ShowFormWithGroups(groups = listOf()) to null)
                }
                is Resource.Failure -> {
                    Timber.i("group flow collection returns error")
                    emit(PlanningMutation.ShowError(exception = it.exception) to null)
                }
                is Resource.Success -> {
                    Timber.i("group flow collection successful")
                    emit(PlanningMutation.ShowFormWithGroups(groups = it.data) to null)
                }
            }
        }
    }


    // refreshing view (= load lists from backend)
    private suspend fun refreshLists() = listRepository.refreshItemsInLocalDB()

    // refreshing view (= load shops from backend)
    private suspend fun refreshShops() = shopRepository.refreshItemsInLocalDB()

    // refreshing view (= load products from backend)
    private suspend fun refreshProducts() = productRepository.refreshItemsInLocalDB()


    // illegal swipe transition --> vibrate phone
    private fun illegalSwipeAction() {
        val vib = context.getSystemService(Vibrator::class.java)
        vibrateDevice(vib, 150)
    }

}

