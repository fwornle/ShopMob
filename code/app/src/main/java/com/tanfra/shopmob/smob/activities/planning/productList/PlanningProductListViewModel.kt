package com.tanfra.shopmob.smob.activities.planning.productList

import android.app.Application
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.tanfra.shopmob.base.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Resource


class PlanningProductListViewModel(
    app: Application,
    private val repoFlow: SmobProductDataSource
) : BaseViewModel(app) {

    // list that holds the smob data items to be displayed on the UI
    // ... flow, converted to LiveData --> data changes in the backend are observed
    var smobProduct = repoFlow.getAllSmobProducts().asLiveData()


    /**
     * Get all the smob items from the DataSource and add them to smobList to be shown on the UI
     * ... or show the error, if any
     *
     * Note: since the conversion to the 'flow' based model, this type of 'manual refreshing' is
     *       no longer necessary --> possibly omit? (fw-220104)
     */
    fun loadProductItems() {

        // activate loading spinner via Resource.status = LOADING
        smobProduct = liveData { Resource.loading(listOf<SmobProductATO>()) }

        // (re-)fetch all shopping lists
        // ... also sets the Resource.status to SUCCESS/ERROR --> deactivates loading spinner
        smobProduct = repoFlow.getAllSmobProducts().asLiveData()

        // handle potential errors
        smobProduct.value?.let {
            if(it.status == Status.ERROR) showSnackBar.value = it.message!!
        }

        // check if no data has to be shown
        updateShowNoData()

    }  // loadProductItems

    /**
     * Inform the user that the list is empty
     */
    private fun updateShowNoData() {
        showNoData.value = (smobProduct.value?.status == Status.SUCCESS && smobProduct.value?.data!!.isEmpty())
    }

}