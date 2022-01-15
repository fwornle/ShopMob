package com.tanfra.shopmob.smob.ui.planning.productEdit

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.base.BaseViewModel
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import com.tanfra.shopmob.smob.data.local.utils.ActivityStatus
import com.tanfra.shopmob.smob.data.local.utils.ProductCategory
import com.tanfra.shopmob.smob.data.local.utils.ProductMainCategory
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductDataSource
import kotlinx.coroutines.launch

class PlanningProductEditViewModel(
    val app: Application,
    private val productDataSource: SmobProductDataSource
    ) : BaseViewModel(app) {

    val smobProductName = MutableLiveData<String?>()
    val smobProductDescription = MutableLiveData<String?>()
    val smobProductImageUrl = MutableLiveData<String?>()
    val smobProductCategory = MutableLiveData<ProductCategory?>()
    val smobProductActivity = MutableLiveData<ActivityStatus?>()


    /**
     * Clear the live data objects to start fresh next time the view model gets called
     */
    fun onClear() {
        smobProductName.value = null
        smobProductDescription.value = null
        smobProductImageUrl.value = null
        smobProductCategory.value = null
        smobProductActivity.value = null
    }

    /**
     * Validate the entered data then saves the smob product to the DataSource
     */
    fun validateAndSaveSmobItem(shopMobData: SmobProductATO) {
        if (validateEnteredData(shopMobData)) {
            saveSmobProductItem(shopMobData)
        }
    }

    /**
     * Save the smob product to the data source
     */
    private fun saveSmobProductItem(smobProductData: SmobProductATO) {
        showLoading.value = true
        viewModelScope.launch {
            with(productDataSource) {
                saveSmobProductItem(
                    SmobProductATO(
                        smobProductData.id,
                        smobProductData.itemStatus,
                        smobProductData.itemPosition,
                        smobProductData.name,
                        smobProductData.description,
                        smobProductData.imageUrl,
                        smobProductData.category,
                        smobProductData.activity,
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
    private fun validateEnteredData(shopMobData: SmobProductATO): Boolean {
        if (shopMobData.name.isEmpty()) {
            showSnackBarInt.value = R.string.err_enter_title
            return false
        }

        if (shopMobData.category.main.equals(ProductMainCategory.OTHER) ) {
            showSnackBarInt.value = R.string.err_select_location
            return false
        }
        return true
    }
}