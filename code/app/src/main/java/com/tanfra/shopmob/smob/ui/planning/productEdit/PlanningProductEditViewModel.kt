package com.tanfra.shopmob.smob.ui.planning.productEdit

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.local.utils.*
import com.tanfra.shopmob.smob.ui.base.BaseViewModel
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.utils.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.Double.NaN

// shared ViewModel: ProductEdit & ShopList
class PlanningProductEditViewModel(
    val app: Application,
    private val productDataSource: SmobProductDataSource,
    val shopDataSource: SmobShopDataSource,
    ) : BaseViewModel(app) {

//    var smobProductName = MutableLiveData<String?>()
//    var smobProductDescription = MutableLiveData<String?>()
//    var smobProductImageUrl = MutableLiveData<String?>()
//    val smobProductCategory = MutableLiveData<ProductCategory>()
//    val smobProductActivity = MutableLiveData<ActivityStatus>()
//
//    // this will be overwritten from within the shopList, as soon as the user selects a shop from the list
//    // ... it remains active until a new shop is chosen
//    var selectedShop = MutableLiveData<SmobShopATO>()
//
//    // default values
//    init {
//        selectedShop.value = SmobShopATO(
//            "no shop selected yet (id)",
//            SmobItemStatus.NEW,
//            -1L,
//            "no shop selected yet",
//            "no shop selected yet",
//            "no shop selected yet",
//            ShopLocation(NaN, NaN),
//            ShopType.INDIVIDUAL,
//            ShopCategory.OTHER,
//            listOf(),
//        )
//
//        smobProductCategory.value = ProductCategory(ProductMainCategory.OTHER, ProductSubCategory.OTHER)
//        smobProductActivity.value = ActivityStatus("not yet defined", 0L)
//    }
//
//    /**
//     * Clear the live data objects to start fresh next time the view model gets called
//     */
//    fun onClear() {
//        smobProductName.value = null
//        smobProductDescription.value = null
//        smobProductImageUrl.value = null
//    }
//
//    /**
//     * Validate the entered data then saves the smob product to the DataSource
//     */
//    fun validateAndSaveSmobItem(shopMobData: SmobProductATO) {
//        if (validateEnteredData(shopMobData)) {
//            saveSmobProductItem(shopMobData)
//        }
//    }
//
//    /**
//     * Save the smob product to the data source
//     */
//    private fun saveSmobProductItem(smobProductData: SmobProductATO) {
//        showLoading.value = true
//        viewModelScope.launch {
//            productDataSource.saveSmobProduct(smobProductData)
//            showLoading.value = false
//
//            showToast.value = app.getString(R.string.smob_item_saved)
//            navigationCommand.value = NavigationCommand.Back
//        }
//    }
//
//    /**
//     * Validate the entered data and show error to the user if there's any invalid data
//     */
//    private fun validateEnteredData(shopMobData: SmobProductATO): Boolean {
//
//        // need product name
//        if (shopMobData.name.isEmpty()) {
//            showSnackBarInt.value = R.string.err_enter_name
//            return false
//        }
//
//        // need product main category (FOODS)
//        if (shopMobData.category.main == ProductMainCategory.OTHER) {
//            showSnackBarInt.value = R.string.err_enter_catMain
//            return false
//        }
//
//        // need product sub category (DAIRY)
//        if (shopMobData.category.sub == ProductSubCategory.OTHER) {
//            showSnackBarInt.value = R.string.err_enter_catSub
//            return false
//        }
//
//        // need shop category (SUPERMARKET)
//        if (shopMobData.inShop.category == ShopCategory.OTHER) {
//            showSnackBarInt.value = R.string.err_select_shop
//            return false
//        }
//
//        // all good --> validation passed
//        return true
//    }
//
//
//    // (ex) ShopListViewModel contents ------------------------------------------------
//    // (ex) ShopListViewModel contents ------------------------------------------------
//    // (ex) ShopListViewModel contents ------------------------------------------------
//
//    // list that holds the smob data items to be displayed on the UI
//    // ... flow, converted to StateFlow --> data changes in the backend are observed
//    // ... ref: https://medium.com/androiddevelopers/migrating-from-livedata-to-kotlins-flow-379292f419fb
//    //var smobList = repoFlow.getAllSmobLists().asLiveData()
//    val smobList = shopDataSource.getAllSmobShops().stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = Resource.loading(null)
//    )
//
//
//    /**
//     * update all items in the local DB by querying the backend - triggered on "swipe down"
//     */
//    @ExperimentalCoroutinesApi
//    fun swipeRefreshDataInLocalDB() {
//
//        // user is impatient - trigger update of local DB from net
//        viewModelScope.launch {
//
//            // update backend DB (from net API)
//            shopDataSource.refreshDataInLocalDB()
//
//            smobList.collect {
//
//                if(it.status == Status.ERROR) {
//                    showSnackBar.value = it.message!!
//                }
//
//                // check if the "no data" symbol has to be shown (empty list)
//                updateShowNoData(it)
//            }
//
//        }
//
//    }  // swipeRefreshDataInLocalDB
//
//    /**
//     * Inform the user that the list is empty
//     */
//    @ExperimentalCoroutinesApi
//    private fun updateShowNoData(smobListNewest: Resource<List<*>>) {
//        showNoData.value = (smobListNewest.status == Status.SUCCESS && smobListNewest.data!!.isEmpty())
//    }

}