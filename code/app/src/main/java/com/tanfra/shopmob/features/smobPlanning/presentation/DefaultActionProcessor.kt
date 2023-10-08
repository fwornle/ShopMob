package com.tanfra.shopmob.features.smobPlanning.presentation

import com.tanfra.shopmob.features.common.arch.ActionProcessor
import com.tanfra.shopmob.features.common.monitor.ConnectivityMonitor
import com.tanfra.shopmob.features.common.monitor.ConnectivityStatus
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Action
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Event
import com.tanfra.shopmob.features.smobPlanning.presentation.model.Mutation
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobListRepository
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class DefaultActionProcessor(
    private val listRepository: SmobListRepository,
    private val connectivityMonitor: ConnectivityMonitor,
) : ActionProcessor<Action, Mutation, Event> {

    override fun invoke(action: Action): Flow<Pair<Mutation?, Event?>> =
        if (action is Action.CheckConnectivity) {
            checkConnectivity()
        } else flow {
            when (action) {
                Action.LoadLists -> load()
                Action.ReloadLists -> reload()
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
    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.load() {
        emit(Mutation.ShowLoader to null)

        listRepository.getSmobItems().collect {
            when(it) {
                Resource.Empty -> {
                    Timber.i("list flow collection returns empty")
                    emit(Mutation.ShowContent(lists = listOf()) to null)
                }
                is Resource.Failure -> {
                    Timber.i("list flow collection returns error")
                    emit(Mutation.ShowError(exception = it.exception) to null)
                }
                is Resource.Success -> {
                    Timber.i("list flow collection successful")
                    emit(Mutation.ShowContent(lists = it.data) to null)
                }
            }
        }
    }


    // refreshing view (= load lists from backend)
    private suspend fun FlowCollector<Pair<Mutation?, Event?>>.reload() {
        emit(null to Event.Refreshing(true))

        // update local DB from backend DB (via net API)
        listRepository.refreshItemsInLocalDB()

        emit(null to Event.Refreshing(false))
    }

}

