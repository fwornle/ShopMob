package com.tanfra.shopmob.smob.activities.planning.lists

import android.app.Application

import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.base.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobListDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.launch


class PlanningListsViewModel(
    app: Application,
    private val repoFlow: SmobListDataSource
) : BaseViewModel(app) {

    // list that holds the smob data items to be displayed on the UI
    // ... flow, converted to StateFlow --> data changes in the backend are observed
    // ... ref: https://medium.com/androiddevelopers/migrating-from-livedata-to-kotlins-flow-379292f419fb
    val smobList = repoFlow.getAllSmobLists().stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5000),
        initialValue = Resource.loading(null)
    )

    /**
     * update all items in the local DB by querying the backend - triggered on "swipe down"
     */
    @ExperimentalCoroutinesApi
    fun swipeRefreshDataInLocalDB() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {

            // update backend DB (from net API)
            repoFlow.refreshDataInLocalDB()

            smobList.collect {

                if(it.status == Status.ERROR) {
                    showSnackBar.value = it.message!!
                }

                // check if the "no data" symbol has to be shown (empty list)
                updateShowNoData(it)
            }

        }

    }  // swipeRefreshDataInLocalDB

    /**
     * Inform the user that the list is empty
     */
    @ExperimentalCoroutinesApi
    private fun updateShowNoData(smobListNewest: Resource<List<*>>) {
        showNoData.value = (smobListNewest.status == Status.SUCCESS && smobListNewest.data!!.isEmpty())
    }
}