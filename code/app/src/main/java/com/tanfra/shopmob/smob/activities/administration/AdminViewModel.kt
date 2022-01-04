package com.tanfra.shopmob.smob.activities.administration

import android.app.Application
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.tanfra.shopmob.base.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobListDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Resource

class AdminViewModel(
    app: Application,
    private val listsFlow: SmobListDataSource
) : BaseViewModel(app) {

    // list that holds the smob data items to be displayed on the UI
    // ... flow, converted to LiveData --> data changes in the backend are observed
    var smobList = listsFlow.getAllSmobLists().asLiveData()


    /**
     * Get all the smob items from the DataSource and add them to the smobItemList to be shown on
     * the UI - or show error, if any
     *
     * Note: since the conversion to the 'flow' based model, this type of 'manual refreshing' is
     *       no longer necessary --> possibly omit? (fw-220104)
     */
    fun loadListItems() {

        // activate loading spinner via Resource.status = LOADING
        smobList = liveData { Resource.loading(listOf<SmobListATO>()) }

        // (re-)fetch all shopping lists
        // ... also sets the Resource.status to SUCCESS/ERROR --> deactivates loading spinner
        smobList = listsFlow.getAllSmobLists().asLiveData()

        // handle potential errors
        smobList.value?.let {
            if(it.status == Status.ERROR) showSnackBar.value = it.message!!
        }

        // check if no data has to be shown
        updateShowNoData()

    }  // loadListItems

    /**
     * Inform the user that the list is empty
     */
    private fun updateShowNoData() {
        showNoData.value = (smobList.value?.status == Status.SUCCESS && smobList.value?.data!!.isEmpty())
    }

}