package com.tanfra.shopmob.smob.ui.planning.productList

import android.app.Application
import androidx.lifecycle.*
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.local.utils.*
import com.tanfra.shopmob.smob.data.repo.ato.*
import com.tanfra.shopmob.smob.ui.base.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobListDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.launch
import timber.log.Timber

class PlanningProductListViewModel(
    val app: Application,
    val listDataSource: SmobListDataSource,  // public, as used (externally) to update the smobList
    private val productDataSource: SmobProductDataSource,
    val shopDataSource: SmobShopDataSource,
    ) : BaseViewModel(app) {

    // current list ID
    var currListId: String? = null

    // StateFlow variables to maintain 'latest fetched value'
    // ... connected to flow --> observes the data source and updates on changes there
    private val _smobList = MutableStateFlow<Resource<SmobListATO?>>(Resource.loading(null))
    private val _smobListItems = MutableStateFlow<Resource<List<SmobProductATO>?>>(Resource.loading(null))

    // combination of both flows (SmobList & product items on that list)
    private val _smobListItemsWithStatus = MutableStateFlow<List<SmobProductOnListATO>?>(listOf())

    // public getters
    fun getSmobList(): StateFlow<Resource<SmobListATO?>> = _smobList
    fun getSmobListItems(): StateFlow<Resource<List<SmobProductATO>?>> = _smobListItems
    fun getSmobListItemsWithStatus(): StateFlow<List<SmobProductOnListATO>?> = _smobListItemsWithStatus


    /**
     * collect the flow of the upstream list the user just selected
     */
    @ExperimentalCoroutinesApi
    fun fetchSmobList() {

        // list ID set yet?
        currListId?.let { id ->

            // collect flow
            viewModelScope.launch {

                // flow terminator
                listDataSource.getSmobList(id)
                    .catch { e ->
                        // previously unhandled exception (= not handled at Room level)
                        // --> catch it here and represent in Resource status
                        _smobList.value = Resource.error(e.toString(), null)
                        showSnackBar.value = _smobList.value.message
                    }
                    .collect {
                        // no exception during flow collection
                        when(it.status) {
                            Status.SUCCESS -> {
                                // --> store successfully received data in StateFlow value
                                _smobList.value = it
                            }
                            Status.ERROR -> {
                                // these are errors handled at Room level --> display
                                showSnackBar.value = it.message
                                _smobList.value = it  // still return Resource value (w/h error)
                            }
                            Status.LOADING -> {
                                // could control visibility of progress bar here
                            }
                        }
                    }

            }  // coroutine

        } // listId set

    }  // fetchSmobList


    /**
     * collect the flow of the list of items for the upstream list the user just selected
     */
    @ExperimentalCoroutinesApi
    fun fetchSmobListItems() {

        // list ID set yet?
        currListId?.let { id ->

            // collect flow
            viewModelScope.launch {

                // flow terminator
                productDataSource.getSmobProductsByListId(id)
                    .catch { e ->
                        // previously unhandled exception (= not handled at Room level)
                        // --> catch it here and represent in Resource status
                        _smobListItems.value = Resource.error(e.toString(), null)
                        showSnackBar.value = _smobListItems.value.message
                    }
                    .collect {
                        // no exception during flow collection
                        when(it.status) {
                            Status.SUCCESS -> {
                                // --> store successfully received data in StateFlow value
                                _smobListItems.value = it
                                updateShowNoData(it)
                            }
                            Status.ERROR -> {
                                // these are errors handled at Room level --> display
                                showSnackBar.value = it.message
                                _smobListItems.value = it  // still return Resource value (w/h error)
                            }
                            Status.LOADING -> {
                                // could control visibility of progress bar here
                            }
                        }
                    }

            }  // coroutine

        }  // listId set

    }  // fetchSmobListItems


    /**
     * combine the two flows (products, shopping list [product status]) and turn into StateFlow
     */
    @ExperimentalCoroutinesApi
    fun fetchCombinedFlows() {

        // list ID set yet?
        currListId?.let { id ->

            val itemsFlow = productDataSource.getSmobProductsByListId(id)
            val listFlow = listDataSource.getSmobList(id)

            // collect flow
            viewModelScope.launch {

                itemsFlow.combine(listFlow) { items, list ->

                    // unwrap list (from Resource)
                    list.data?.let { rawList ->

                        // evaluate/unwrap Resource
                        when (items.status) {

                            Status.SUCCESS -> {
                                // received the items on the list alright --> process
                                items.data?.map { product ->

                                    // at this point, the products on the shopping lists have been properly
                                    // received --> implies that the list itself is also available
                                    // output merged data type (with product item status)

                                    // fetch product details from 'items' list on the smobList
                                    val productOnList =
                                        rawList.items.first { item -> item.id == product.id }

                                    SmobProductOnListATO(
                                        id = product.id,
                                        itemStatus = productOnList.status,
                                        itemPosition = productOnList.listPosition,
                                        productName = product.name,
                                        productDescription = product.description,
                                        productImageUrl = product.imageUrl,
                                        productCategory = product.category,
                                        productActivity = product.activity,
                                        listId = rawList.id,
                                        listName = rawList.name,
                                        listDescription = rawList.description,
                                        listItems = rawList.items,
                                        listMembers = rawList.members,
                                        listLifecycle = rawList.lifecycle,
                                    )
                                }
                            }
                            else -> {
                                // Status.LOADING or Status.ERROR -> do nothing
                                null
                            }

                        }  // when

                    }  // unwrap list

                }  // combine (flows)
                    .catch { e ->
                        // previously unhandled exception (= not handled at Room level) --> console
                        Timber.e(e.toString())
                    }
                    .collect {
                        // store collected flow data in StateFlow value
                        _smobListItemsWithStatus.value = it
                    }

            }  // viewModelScope.launch

        }  // listId set

    }  //  combineFlowsAndConvertToStateFlow


    /**
     * update all items in the local DB by querying the backend - triggered on "swipe down"
     */
    @ExperimentalCoroutinesApi
    fun swipeRefreshProductDataInLocalDB() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {

            // update backend DB (from net API)
            productDataSource.refreshDataInLocalDB()

            // collect flow to update StateFlow with current value from DB
            fetchSmobList()
            fetchSmobListItems()
            fetchCombinedFlows()

            // check if the "no data" symbol has to be shown (empty list)
            updateShowNoData(_smobListItems.value)

        }

    }  // swipeRefreshProductDataInLocalDB

    /**
     * Inform the user that the list is empty
     */
    @ExperimentalCoroutinesApi
    private fun updateShowNoData(smobListNewest: Resource<List<*>?>) {
        showNoData.value = (
                smobListNewest.status == Status.SUCCESS && smobListNewest.data!!.isEmpty() ||
                smobListNewest.status == Status.SUCCESS && smobListNewest.data!!.all {
                    (it as Ato).itemStatus == SmobItemStatus.DELETED
                }
                )
    }


    // (ex) PlanningProductEditViewModel contents ------------------------------------------------
    // (ex) PlanningProductEditViewModel contents ------------------------------------------------
    // (ex) PlanningProductEditViewModel contents ------------------------------------------------

    var smobProductName = MutableLiveData<String?>()
    var smobProductDescription = MutableLiveData<String?>()
    var smobProductImageUrl = MutableLiveData<String?>()
    val smobProductCategory = MutableLiveData<ProductCategory>()

    // this will be overwritten from within the shopList, as soon as the user selects a shop from the list
    // ... it remains active until a new shop is chosen
    var selectedShop = MutableLiveData<SmobShopATO>()

    // default values
    init {
        selectedShop.value = SmobShopATO(
            "no product selected yet (id)",
            SmobItemStatus.NEW,
            -1L,
            "",
            "",
            "",
            ShopLocation(0.0, 0.0),
            ShopType.INDIVIDUAL,
            ShopCategory.OTHER,
            listOf(),
        )

        smobProductCategory.value = ProductCategory(ProductMainCategory.OTHER, ProductSubCategory.OTHER)
    }

    /**
     * Clear the live data objects to start fresh next time the view model gets called
     */
    fun onClear() {
        smobProductName.value = null
        smobProductDescription.value = null
        smobProductImageUrl.value = null
    }

    /**
     * Validate the entered data then saves the smob product to the DataSource
     */
    fun validateAndSaveSmobItem(shopMobData: SmobProductATO) {
        if (validateEnteredData(shopMobData)) {
            saveSmobProductItem(shopMobData, true)
        }
    }

    /**
     * Save the smob product to the data source
     */
    private fun saveSmobProductItem(smobProductData: SmobProductATO, navBack: Boolean) {
        showLoading.value = true
        viewModelScope.launch {
            productDataSource.saveSmobProduct(smobProductData)
            showLoading.value = false

            // update StateFlow values by re-fetching flows from local DB
            fetchSmobList()
            fetchSmobListItems()
            fetchCombinedFlows()

            // only travel back, if requested
            if(navBack) navigationCommand.value = NavigationCommand.Back
        }

    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
    private fun validateEnteredData(shopMobData: SmobProductATO): Boolean {

        // need product name
        if (shopMobData.name.isEmpty()) {
            showSnackBarInt.value = R.string.err_enter_name
            return false
        }

        // need product main category (FOODS)
        if (shopMobData.category.main == ProductMainCategory.OTHER) {
            showSnackBarInt.value = R.string.err_enter_catMain
            return false
        }

        // need product sub category (DAIRY)
        if (shopMobData.category.sub == ProductSubCategory.OTHER) {
            showSnackBarInt.value = R.string.err_enter_catSub
            return false
        }

        // need shop category (SUPERMARKET)
        if (shopMobData.inShop.category == ShopCategory.OTHER) {
            showSnackBarInt.value = R.string.err_select_shop
            return false
        }

        // all good --> validation passed
        return true
    }


    // (ex) ShopListViewModel contents ------------------------------------------------
    // (ex) ShopListViewModel contents ------------------------------------------------
    // (ex) ShopListViewModel contents ------------------------------------------------

    // list that holds the SmobShop data items to be displayed on the UI
    // ... flow, converted to StateFlow --> data changes in the backend are observed
    val smobShopList = shopDataSource.getAllSmobShops().stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5000),
        initialValue = Resource.loading(null)
    )


    /**
     * update all items in the local DB by querying the backend - triggered on "swipe down"
     */
    @ExperimentalCoroutinesApi
    fun swipeRefreshShopDataInLocalDB() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {

            // update backend DB (from net API)
            shopDataSource.refreshDataInLocalDB()

            smobShopList.collect {

                if(it.status == Status.ERROR) {
                    showSnackBar.value = it.message!!
                }

                // check if the "no data" symbol has to be shown (empty list)
                updateShowNoData(it)
            }

        }

    }  // swipeRefreshShopDataInLocalDB

    /**
     * Save the smob product to the data source
     */
    fun saveSmobListItem(smobListData: SmobListATO, goBack: Boolean) {
        showLoading.value = true
        viewModelScope.launch {
            listDataSource.saveSmobList(smobListData)
            showLoading.value = false

            showToast.value = app.getString(R.string.smob_list_saved)

            // only travel back, if requested
            if(goBack) navigationCommand.value = NavigationCommand.Back
        }
    }

}
