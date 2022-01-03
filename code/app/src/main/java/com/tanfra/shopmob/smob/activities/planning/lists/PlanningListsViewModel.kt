package com.tanfra.shopmob.smob.activities.planning.lists

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.base.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobListDataSource
import kotlinx.coroutines.launch
import timber.log.Timber

class PlanningListsViewModel(
    app: Application,
    private val listsDataSource: SmobListDataSource
) : BaseViewModel(app) {

    // list that holds the smob data items to be displayed on the UI
    val smobList = MutableLiveData<List<SmobListATO>>()

    /**
     * Get all the smob items from the DataSource and add them to the smobItemList to be shown on
     * the UI - or show error, if any
     */
    @Suppress("UNCHECKED_CAST")
    fun loadListItems() {

        // activate loading spinner
        showLoading.value = true

        // interacting with the dataSource has to be through a coroutine
        viewModelScope.launch {

            // fetch all shopping lists
            val result = listsDataSource.getAllSmobLists()

            // deactivate loading spinner
            showLoading.postValue(false)

            // set LiveData to update UI
            when (result.status) {
                Status.SUCCESS -> {
                    // only set new LiveData, if data has been received - otherwise: empty list
                    smobList.value = result.data ?: listOf()
                }
                Status.ERROR ->
                    showSnackBar.value = result.message!!
                else -> {
                    // (still) LOADING -- this should never be reached
                    Timber.w("Stuck in state ${result.status} (should never happen)")
                }
            }

            // check if no data has to be shown
            invalidateShowNoData()
        }

    }  // loadListItems


    /**
     * Inform the user that the list is empty
     */
    private fun invalidateShowNoData() {
        showNoData.value = smobList.value == null || smobList.value!!.isEmpty()
    }

}