package com.tanfra.shopmob.smob.activities.details

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.base.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailsViewModel(
    app: Application,
    private val smobProductDataSource: SmobProductDataSource,
    private val smobShopDataSource: SmobShopDataSource,
) : BaseViewModel(app) {

    // item to be displayed on the UI
    val smobShopDetailsItem = MutableLiveData<SmobShopATO?>()
    val smobProductDetailsItem = MutableLiveData<SmobProductATO?>()

    init {
        smobShopDetailsItem.value = null
        smobProductDetailsItem.value = null
    }

//    /**
//     * Get data for UI display item (Shop)
//     */
//    fun <T> T.fetchItem(id: String) {
//
//        // activate loading spinner
//        showLoading.value = true
//
//        // interacting with the dataSource has to be through a coroutine
//        viewModelScope.launch {
//
//            // fetch item
//            val result = shopDataSource.getSmobShop(id)
//
//            // deactivate loading spinner
//            showLoading.postValue(false)
//
//            // set LiveData to update UI
//            when (result.status) {
//                Status.SUCCESS -> {
//                    // only set new LiveData, if data has been received - otherwise: null
//                    smobDetailsItem.value = result.data
//                }
//                Status.ERROR ->
//                    showSnackBar.value = result.message!!
//                else -> {
//                    // (still) LOADING -- this should never be reached
//                    Timber.w("Stuck in state ${result.status} (should never happen)")
//                }
//            }
//
//        }
//
//    }  // fetchDisplayItem

}