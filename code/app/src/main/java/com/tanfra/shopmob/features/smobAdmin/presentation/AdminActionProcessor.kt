package com.tanfra.shopmob.features.smobAdmin.presentation

import android.content.Context
import android.os.Vibrator
import com.tanfra.shopmob.features.common.arch.ActionProcessor
import com.tanfra.shopmob.features.common.monitor.ConnectivityMonitor
import com.tanfra.shopmob.features.common.monitor.ConnectivityStatus
import com.tanfra.shopmob.features.smobAdmin.presentation.model.AdminAction
import com.tanfra.shopmob.features.smobAdmin.presentation.model.AdminEvent
import com.tanfra.shopmob.features.smobAdmin.presentation.model.AdminMutation
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobGroupRepository
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobListRepository
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.ui.zeUtils.vibrateDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class AdminActionProcessor(
    private val context: Context,
    private val listRepository: SmobListRepository,
    private val groupRepository: SmobGroupRepository,
    private val connectivityMonitor: ConnectivityMonitor,
) : ActionProcessor<AdminAction, AdminMutation, AdminEvent> {

    override fun invoke(action: AdminAction): Flow<Pair<AdminMutation?, AdminEvent?>> =
        if (action is AdminAction.CheckConnectivity) {
            checkConnectivity()
        } else flow {
            when (action) {
                AdminAction.LoadLists -> loadLists()
                AdminAction.LoadGroups -> loadGroups()
                AdminAction.RefreshLists -> refreshLists()
                AdminAction.RefreshGroups -> refreshGroups()
                AdminAction.IllegalSwipe -> illegalSwipeAction()
                else -> {
                    //no-op
                    Timber.i("MVI.UI: ${action.toString().take(50)}... not found in AdminActionProcessor")
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
                    AdminMutation.DismissLostConnection
                } else {
                    AdminMutation.ShowLostConnection
                } to null
            }


    // load lists from local DB
    private suspend fun FlowCollector<Pair<AdminMutation?, AdminEvent?>>.loadLists() {
        emit(AdminMutation.ShowLoader to null)

        listRepository.getSmobItems().collect {
            when(it) {
                Resource.Empty -> {
                    Timber.i("list flow collection returns empty")
                    emit(AdminMutation.ShowLists(lists = listOf()) to null)
                }
                is Resource.Failure -> {
                    Timber.i("list flow collection returns error")
                    emit(AdminMutation.ShowError(exception = it.exception) to null)
                }
                is Resource.Success -> {
                    Timber.i("list flow collection successful")
                    emit(AdminMutation.ShowLists(lists = it.data) to null)
                }
            }
        }
    }


    // load groups from local DB
    private suspend fun FlowCollector<Pair<AdminMutation?, AdminEvent?>>.loadGroups() {
        groupRepository.getSmobItems().collect {
            when(it) {
                Resource.Empty -> {
                    Timber.i("group flow collection returns empty")
                    emit(AdminMutation.ShowFormWithGroups(groups = listOf()) to null)
                }
                is Resource.Failure -> {
                    Timber.i("group flow collection returns error")
                    emit(AdminMutation.ShowError(exception = it.exception) to null)
                }
                is Resource.Success -> {
                    Timber.i("group flow collection successful")
                    emit(AdminMutation.ShowFormWithGroups(groups = it.data) to null)
                }
            }
        }
    }


    // refreshing view (= load lists from backend)
    private suspend fun refreshLists() = listRepository.refreshItemsInLocalDB()

    // refreshing view (= load groups from backend)
    private suspend fun refreshGroups() = groupRepository.refreshItemsInLocalDB()


    // illegal swipe transition --> vibrate phone
    private fun illegalSwipeAction() {
        val vib = context.getSystemService(Vibrator::class.java)
        vibrateDevice(vib, 150)
    }

}

