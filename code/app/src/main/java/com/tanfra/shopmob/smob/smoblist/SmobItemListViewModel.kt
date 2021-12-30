package com.tanfra.shopmob.smob.smoblist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.base.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobItemDataSource
import com.tanfra.shopmob.smob.data.repo.Result
import com.tanfra.shopmob.smob.types.SmobItem
import kotlinx.coroutines.launch

class SmobItemListViewModel(
    app: Application,
    private val itemDataSource: SmobItemDataSource
) : BaseViewModel(app) {

    // list that holds the smob data items to be displayed on the UI
    val smobItemList = MutableLiveData<List<SmobItem>>()

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

            // fetch all items of the active shopping list
            val result = itemDataSource.getSmobItems()

            // deactivate loading spinner
            showLoading.postValue(false)

            // convert data from DTO format to app format
            when (result) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<SmobItem>()
                    dataList.addAll((result.data as List<SmobItem>).map { smobItem ->
                        // map the smob item data from DB format (SmobItemDTO) to the format used
                        // when displaying on the UI
                        SmobItem(
                            smobItem.title,
                            smobItem.description,
                            smobItem.location,
                            smobItem.latitude,
                            smobItem.longitude,
                            smobItem.id
                        )
                    })
                    smobItemList.value = dataList
                }
                is Result.Error ->
                    showSnackBar.value = result.message!!
            }

            // check if no data has to be shown
            invalidateShowNoData()
        }

    }


    /**
     * Inform the user that there's not any data if the smobItemList is empty
     */
    private fun invalidateShowNoData() {
        showNoData.value = smobItemList.value == null || smobItemList.value!!.isEmpty()
    }

}