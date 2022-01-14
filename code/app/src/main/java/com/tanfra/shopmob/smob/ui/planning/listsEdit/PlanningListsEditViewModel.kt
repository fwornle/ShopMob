package com.tanfra.shopmob.smob.ui.planning.listsEdit

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.base.BaseViewModel
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobListDataSource
import kotlinx.coroutines.launch

class PlanningListsEditViewModel(
    val app: Application,
    private val listsDataSource: SmobListDataSource
) : BaseViewModel(app) {

    val smobListName = MutableLiveData<String?>()
    val smobListDescription = MutableLiveData<String?>()
    val smobListImageUrl = MutableLiveData<String?>()

    /**
     * Clear the live data objects to start fresh next time the view model gets called
     */
    fun onClear() {
        smobListName.value = null
        smobListDescription.value = null
        smobListImageUrl.value = null
    }

    /**
     * Validate the entered data then saves the smobList to the DataSource
     */
    fun validateAndSaveSmobList(shopMobData: SmobListATO) {
        if (validateEnteredData(shopMobData)) {
            saveSmobListItem(shopMobData)
        }
    }

    /**
     * Save the smobList item to the data source
     */
    private fun saveSmobListItem(smobListData: SmobListATO) {
        showLoading.value = true
        viewModelScope.launch {
            // store in local DB (and sync to server)
            listsDataSource.saveSmobList(smobListData)
        }
        showLoading.value = false

        showToast.value = app.getString(R.string.smob_item_saved)
        navigationCommand.value = NavigationCommand.Back
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
    private fun validateEnteredData(shopMobData: SmobListATO): Boolean {
        if (shopMobData.name.isEmpty()) {
            showSnackBarInt.value = R.string.err_enter_title
            return false
        }

        return true
    }
}
