package com.tanfra.shopmob.smob.activities.planning.shopList

import android.app.Application
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.tanfra.shopmob.base.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Resource


class PlanningShopListViewModel(
    app: Application,
    private val repoFlow: SmobShopDataSource
) : BaseViewModel(app) {

    // list that holds the smob data items to be displayed on the UI
    // ... flow, converted to LiveData --> data changes in the backend are observed
    var smobShop = repoFlow.getAllSmobShops().asLiveData()


    /**
     * Get all the smob items from the DataSource and add them to smobList to be shown on the UI
     * ... or show the error, if any
     *
     * Note: since the conversion to the 'flow' based model, this type of 'manual refreshing' is
     *       no longer necessary --> possibly omit? (fw-220104)
     */
    fun loadShopItems() {

        // activate loading spinner via Resource.status = LOADING
        smobShop = liveData { Resource.loading(listOf<SmobShopATO>()) }

        // (re-)fetch all shopping lists
        // ... also sets the Resource.status to SUCCESS/ERROR --> deactivates loading spinner
        smobShop = repoFlow.getAllSmobShops().asLiveData()

        // handle potential errors
        smobShop.value?.let {
            if(it.status == Status.ERROR) showSnackBar.value = it.message!!
        }

        // check if no data has to be shown
        updateShowNoData()

    }  // loadShopItems

    /**
     * Inform the user that the list is empty
     */
    private fun updateShowNoData() {
        showNoData.value = (smobShop.value?.status == Status.SUCCESS && smobShop.value?.data!!.isEmpty())
    }

}