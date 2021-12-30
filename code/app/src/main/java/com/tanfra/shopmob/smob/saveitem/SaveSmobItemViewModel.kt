package com.tanfra.shopmob.smob.saveitem

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.base.BaseViewModel
import com.tanfra.shopmob.base.NavigationCommand
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobItemDataSource
import com.tanfra.shopmob.smob.data.repo.ato.SmobItemATO
import kotlinx.coroutines.launch

// note: all three concrete viewModels (SmobItemList, SaveSmobItem, SelectLocation) inherit from
//       a common "base viewModel" (BaseViewModel)
//       ... which defines the LiveData/Event elements shared by all three (derived) viewModels
class SaveSmobItemViewModel(val app: Application, private val itemDataSource: SmobItemDataSource) :
    BaseViewModel(app) {

    val smobItemTitle = MutableLiveData<String?>()
    val smobItemDescription = MutableLiveData<String?>()
    val smobItemSelectedLocationStr = MutableLiveData<String?>()
    val smobItemlatitude = MutableLiveData<Double?>()
    val smobItemlongitude = MutableLiveData<Double?>()

    // log the state of geoFencing
    //
    // note: nullable -> when the user changes the permissions from outside the app, they
    //       necessarily need to put the app into the background. This calls onClear (see below)
    //       which resets a possibly previously set geoFencingOn
    val geoFencingOn = MutableLiveData<Boolean?>()


    /**
     * Clear the live data objects to start fresh next time the view model gets called
     */
    fun onClear() {
        smobItemTitle.value = null
        smobItemDescription.value = null
        smobItemSelectedLocationStr.value = null
        smobItemlatitude.value = null
        smobItemlongitude.value = null
        geoFencingOn.value = null
    }

    /**
     * Validate the entered data then saves the smob item to the DataSource
     */
    fun validateAndSaveSmobItem(shopMobData: SmobItemATO) {
        if (validateEnteredData(shopMobData)) {
            saveSmobItem(shopMobData)
        }
    }

    /**
     * Save the smob item to the data source
     */
    private fun saveSmobItem(shopMobData: SmobItemATO) {
        showLoading.value = true
        viewModelScope.launch {
            with(itemDataSource) {
                saveSmobItem(
                    SmobItemATO(
                        shopMobData.title,
                        shopMobData.description,
                        shopMobData.location,
                        shopMobData.latitude,
                        shopMobData.longitude,
                        shopMobData.id
                    )
                )
            }
            showLoading.value = false
            showToast.value = app.getString(R.string.smob_item_saved)
            navigationCommand.value = NavigationCommand.Back
        }
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
    private fun validateEnteredData(shopMobData: SmobItemATO): Boolean {
        if (shopMobData.title.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_title
            return false
        }

        if (shopMobData.location.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_select_location
            return false
        }
        return true
    }
}