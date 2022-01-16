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
import java.lang.Double.NaN

class PlanningShopEditViewModel(
    val app: Application,
    private val shopDataSource: SmobShopDataSource
    ) : BaseViewModel(app) {

    val smobShopName = MutableLiveData<String?>()
    val smobShopDescription = MutableLiveData<String?>()
    val smobShopLocation = MutableLiveData<ShopLocation?>()
    val smobShopType = MutableLiveData<ShopType?>()
    val smobShopCategory = MutableLiveData<ShopCategory?>()
    val smobShopBusiness = MutableLiveData<List<String>?>()

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
            "no shop selected yet (id)",
            SmobItemStatus.NEW,
            -1L,
            "no shop selected yet",
            "no shop selected yet",
            "no shop selected yet",
            ShopLocation(NaN, NaN),
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
        smobShopName.value = null
        smobShopDescription.value = null
        smobShopLocation.value = null
        smobShopType.value = null
        smobShopCategory.value = null
        smobShopBusiness.value = null

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
            shopDataSource.saveSmobShop(shopData)
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
            shopData.location.latitude.isNaN() ||
            shopData.location.longitude.isNaN()
        ) {
            showSnackBarInt.value = R.string.err_select_location
            return false
        }
        return true
    }
}