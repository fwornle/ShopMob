package com.tanfra.shopmob.features.smobPlanning.presentation.view.shops.addNewItem

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.ShopCategory
import com.tanfra.shopmob.smob.data.types.ShopLocation
import com.tanfra.shopmob.smob.data.types.ShopType
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseViewModel
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationCommand
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobShopRepository
import kotlinx.coroutines.launch

class PlanningShopsAddNewItemViewModel(
    val app: Application,
    private val shopDataSource: SmobShopRepository
    ) : BaseViewModel(app) {

    var locatedShop = MutableLiveData<SmobShopATO>()

    // log the state of geoFencing
    //
    // note: nullable -> when the user changes the permissions from outside the app, they
    //       necessarily need to put the app into the background. This calls onClear (see below)
    //       which resets a possibly previously set geoFencingOn
    val geoFencingOn = MutableLiveData<Boolean?>()

    // default values
    init {
        locatedShop.value = SmobShopATO(
            "invalid-id-shop",
            ItemStatus.NEW,
            -1L,
            "",
            "",
            "",
            ShopLocation(0.0, 0.0),
            ShopType.INDIVIDUAL,
            ShopCategory.OTHER,
            listOf(),
        )

        // initialize the rest...
        onClear()

    }

    /**
     * Clear the live data objects to start fresh next time the view model gets called
     */
    fun onClear() {
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
            shopDataSource.saveSmobItem(shopData)
            showLoading.value = false

            showToast.value = app.getString(R.string.smob_shop_saved)
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
            shopData.location.latitude == 0.0 ||
            shopData.location.longitude == 0.0
        ) {
            showSnackBarInt.value = R.string.err_select_location
            return false
        }
        return true
    }
}