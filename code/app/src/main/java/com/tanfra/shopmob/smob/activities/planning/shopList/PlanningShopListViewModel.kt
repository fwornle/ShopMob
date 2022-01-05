package com.tanfra.shopmob.smob.activities.planning.shopList

import android.app.Application
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.base.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class PlanningShopListViewModel(
    app: Application,
    private val repoFlow: SmobShopDataSource
) : BaseViewModel(app) {

    // list that holds the smob data items to be displayed on the UI
    // ... flow, converted to StateFlow --> data changes in the backend are observed
    // ... ref: https://medium.com/androiddevelopers/migrating-from-livedata-to-kotlins-flow-379292f419fb
    //var smobList = repoFlow.getAllSmobLists().asLiveData()
    val smobList = repoFlow.getAllSmobShops().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Or Lazily because it's a one-shot
        initialValue = Resource.loading(null)
    )


    /**
     * Get all the smob items from the DataSource and add them to smobList to be shown on the UI
     * ... or show the error, if any
     *
     * Note: since the conversion to the 'flow' based model, this type of 'manual refreshing' is
     *       no longer necessary --> possibly omit? (fw-220104)
     */
    fun loadShopItems() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {
            // update backend DB (from net API)
            repoFlow.refreshDataInLocalDB()
        }

        // handle potential errors
        smobList.value.let {
            if(it.status == Status.ERROR) showSnackBar.value = it.message!!
        }

        // check if no data has to be shown
        updateShowNoData()

    }  // loadShopItems

    /**
     * Inform the user that the list is empty
     */
    private fun updateShowNoData() {
        showNoData.value = (smobList.value.status == Status.SUCCESS && smobList.value.data!!.isEmpty())
    }

}