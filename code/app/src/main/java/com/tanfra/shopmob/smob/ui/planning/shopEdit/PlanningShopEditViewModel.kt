package com.tanfra.shopmob.smob.ui.planning.shopEdit

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.base.BaseViewModel
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import com.tanfra.shopmob.smob.data.local.utils.*
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource
import kotlinx.coroutines.launch

class PlanningShopEditViewModel(
    val app: Application,
    private val shopDataSource: SmobShopDataSource
    ) : BaseViewModel(app) {

    val smobShopName = MutableLiveData<String?>()
    val smobShopDescription = MutableLiveData<String?>()
    val smobShopLocation = MutableLiveData<ShopLocation?>()
    val smobShopType = MutableLiveData<ShopType?>()
    val smobShopCategory = MutableLiveData<ShopCategory?>()
    val smobShopBusiness = MutableLiveData<List<String>>()


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
        smobShopName.value = null
        smobShopDescription.value = null
        smobShopLocation.value = null
        smobShopType.value = null
        smobShopCategory.value = null
        smobShopBusiness.value = listOf()

        geoFencingOn.value = null
    }

    /**
     * Validate the entered data then saves the smob item to the DataSource
     */
    fun validateAndSaveSmobItem(shopMobData: SmobShopATO) {
        if (validateEnteredData(shopMobData)) {
            saveSmobItem(shopMobData)
        }
    }

    /**
     * Save the smob item to the data source
     */
    private fun saveSmobItem(shopData: SmobShopATO) {
        showLoading.value = true
        viewModelScope.launch {
            with(shopDataSource) {
                saveSmobItem(
                    SmobShopATO(
                        shopData.id,
                        shopData.name,
                        shopData.description,
                        shopData.imageUrl,
                        shopData.location,
                        shopData.type,
                        shopData.category,
                        shopData.business,
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
    private fun validateEnteredData(shopData: SmobShopATO): Boolean {
        if (shopData.name.isEmpty()) {
            showSnackBarInt.value = R.string.err_enter_title
            return false
        }

        if (
            shopData.location.latitude.isNaN() ||
            shopData.location.longitude.isNaN()
        ) {
            showSnackBarInt.value = R.string.err_select_location
            return false
        }
        return true
    }
}