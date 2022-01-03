package com.tanfra.shopmob.smob.activities.planning.shopList

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.base.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobListDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for the display of a Smob ShopList.
 */
class PlanningShopListViewModel(
    app: Application,
    private val shopListDataSource: SmobShopDataSource
    ) : BaseViewModel(app) {

    // list that holds the smob data items to be displayed on the UI
    val smobList = MutableLiveData<List<SmobShopATO>>()


//    //
//    private val _myUiState = MutableLiveData<Resource<>>(Result.Loading)
//    val myUiState: LiveData<Resource<UiState>> = _myUiState
//
//    // Load data from a suspend fun and mutate state
//    init {
//        viewModelScope.launch {
//            val result = ...
//            _myUiState.value = result
//        }
//    }

    /**
     * Get all the smob items from the DataSource and add them to the smobItemList to be shown on
     * the UI - or show error, if any
     */
    @Suppress("UNCHECKED_CAST")
    fun loadSmobItems() {

        // activate loading spinner
        showLoading.value = true

        // interacting with the dataSource has to be through a coroutine
        viewModelScope.launch {

            // fetch selected list
            val result = shopListDataSource.getAllSmobShops()

            // deactivate loading spinner
            showLoading.postValue(false)

            // convert data from DTO format to app format
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

    }


    /**
     * Inform the user that there's not any data if the smobItemList is empty
     */
    private fun invalidateShowNoData() {
        showNoData.value = smobList.value == null
    }

}