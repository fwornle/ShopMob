package com.tanfra.shopmob.smob.ui.planning

import android.app.Application
import androidx.lifecycle.*
import com.tanfra.shopmob.R
import com.tanfra.shopmob.SmobApp
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.ProductCategory
import com.tanfra.shopmob.smob.data.types.ProductMainCategory
import com.tanfra.shopmob.smob.data.types.ProductSubCategory
import com.tanfra.shopmob.smob.data.types.ShopCategory
import com.tanfra.shopmob.smob.data.types.ShopLocation
import com.tanfra.shopmob.smob.data.types.ShopType
import com.tanfra.shopmob.smob.data.local.utils.*
import com.tanfra.shopmob.smob.data.net.utils.NetworkConnectionManager
import com.tanfra.shopmob.smob.data.repo.ato.*
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobListRepository
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductRepository
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopRepository
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationCommand
import com.tanfra.shopmob.smob.ui.planning.lists.PlanningListsViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


@OptIn(ExperimentalCoroutinesApi::class)
class PlanningViewModel(
    val app: Application,
    val listDataSource: SmobListRepository,  // public, as used (externally) to update the smobList
    private val productDataSource: SmobProductRepository,
    val shopDataSource: SmobShopRepository,
    ) : BaseViewModel(app), KoinComponent {


    // define PlanningLists UI state as flow
    private val _viewStateLists = MutableStateFlow(PlanningListsViewState())
    val viewStateLists = _viewStateLists.asStateFlow()



    // fetch worker class form service locator
    val networkConnectionManager: NetworkConnectionManager by inject()

    // navigation source - used to be able to have a "modal" list item click handler
    // ... depending on how we got to the list (and, as such, on how we want to use it)
    var navSource = "navDrawer"  // default

    // current list ID and list position (in the list of SmobLists)
    var currListId: String? = null

    // collect the upstream selected smobList as well as the list of SmobProductATO items
    // ... lateinit, as this can only be done once the fragment is created (and the id's are here)
    lateinit var _smobList: Flow<Resource<SmobListATO?>>
    lateinit var smobList: StateFlow<Resource<SmobListATO?>>

    private val _smobList2 = MutableStateFlow<Resource<SmobListATO?>>(Resource.loading(null))
    val smobList2 = _smobList2.asStateFlow()

    lateinit var _smobListItems: Flow<Resource<List<SmobProductATO>?>>
    lateinit var smobListItems: StateFlow<Resource<List<SmobProductATO>?>>

    lateinit var smobListItemsWithStatus: StateFlow<List<SmobProductWithListDataATO>?>


    /**
     * fetch the flow of the upstream list the user just selected
     */
    @ExperimentalCoroutinesApi
    fun fetchSmobListFlow(id: String): Flow<Resource<SmobListATO?>> {
        return listDataSource.getSmobItem(id)
    }

    // convert to StateFlow
    fun smobListFlowToStateFlow(inFlow: Flow<Resource<SmobListATO?>>): StateFlow<Resource<SmobListATO?>> {
        return inFlow.stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5000),
            initialValue = Resource.loading(null)
        )  // StateFlow<...>
    }

    /**
     * fetch the flow of the list of items for the upstream list the user just selected
     */
    @ExperimentalCoroutinesApi
    fun fetchSmobListItemsFlow(id: String): Flow<Resource<List<SmobProductATO>?>> {
        return productDataSource.getSmobProductsByListId(id)
    }

    // convert to StateFlow
    fun smobListItemsFlowToStateFlow(inFlow: Flow<Resource<List<SmobProductATO>?>>): StateFlow<Resource<List<SmobProductATO>?>> {
        return inFlow.stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5000),
            initialValue = Resource.loading(null)
        )  // StateFlow<...>
    }

    /**
     * combine the two flows (products, shopping list [product status]) and turn into StateFlow
     */
    @ExperimentalCoroutinesApi
    fun combineFlowsAndConvertToStateFlow(
        listFlow: Flow<Resource<SmobListATO?>>,
        itemsFlow: Flow<Resource<List<SmobProductATO>?>>,
    ): StateFlow<List<SmobProductWithListDataATO>?> {

        return itemsFlow.combine(listFlow) { items, list ->

            // unwrap list (from Resource)
            list.data?.let { rawList ->

                // evaluate/unwrap Resource
                when(items.status) {

                    Status.SUCCESS -> {
                        // received the items on the list alright --> process
                        items.data?.map { product ->

                            // at this point, the products on the shopping lists have been properly
                            // received --> implies that the list itself is also available
                            // output merged data type (with product item status)

                            // fetch product details from 'items' list on the smobList
                            val productOnList = rawList.items.first { item -> item.id == product.id }

                            SmobProductWithListDataATO(
                                id = product.id,
                                status = productOnList.status,
                                position = productOnList.listPosition,
                                productName = product.name,
                                productDescription = product.description,
                                productImageUrl = product.imageUrl,
                                productCategory = product.category,
                                productActivity = product.activity,
                                productInShop = product.inShop,
                                listId = rawList.id,
                                listStatus = rawList.status,
                                listPosition = rawList.position,
                                listName = rawList.name,
                                listDescription = rawList.description,
                                listItems = rawList.items,
                                listGroups = rawList.groups,
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

        }  // combine(Flow_1, Flow_2)
            .stateIn(
                scope = viewModelScope,
                started = WhileSubscribed(5000),
                initialValue = listOf()
            )  // StateFlow<...>

    }  //  combineFlowsAndConvertToStateFlow



//    // StateFlow variables to maintain 'latest fetched value'
//    // ... connected to flow --> observes the data source and updates on changes there
//    private val _smobList = MutableStateFlow<Resource<SmobListATO?>>(Resource.loading(null))
//    private val _smobListItems = MutableStateFlow<Resource<List<SmobProductATO>?>>(Resource.loading(null))
//
//    // combination of both flows (SmobList & product items on that list)
//    private val _smobListItemsWithStatus = MutableStateFlow<List<SmobProductOnListATO>?>(listOf())
//
//    // public facing read-only StateFlows
//    val smobList = _smobList.asStateFlow()
//    val smobListItems = _smobListItems.asStateFlow()
//    val smobListItemsWithStatus = _smobListItemsWithStatus.asStateFlow()


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
                listDataSource.getSmobItem(id)
                    .catch { e ->
                        // previously unhandled exception (= not handled at Room level)
                        // --> catch it here and represent in Resource status
                        _smobList2.value = Resource.error(e.toString(), null)
                        showSnackBar.value = _smobList2.value.message ?: "(no message)"
                    }
                    .collectLatest {
                        // no exception during flow collection
                        when(it.status) {
                            Status.SUCCESS -> {
                                // --> store successfully received data in StateFlow value
                                _smobList2.value = it
                            }
                            Status.ERROR -> {
                                // these are errors handled at Room level --> display
                                showSnackBar.value = it.message ?: "(no message)"
                                _smobList2.value = it  // still return Resource value (w/h error)
                            }
                            Status.LOADING -> {
                                // could control visibility of progress bar here
                            }
                        }
                    }

            }  // coroutine

        } // listId set

    }  // fetchSmobList


//    /**
//     * collect the flow of the list of items for the upstream list the user just selected
//     */
//    @ExperimentalCoroutinesApi
//    fun fetchSmobListItems() {
//
//        // list ID set yet?
//        currListId?.let { id ->
//
//            // collect flow
//            viewModelScope.launch {
//
//                // flow terminator
//                productDataSource.getSmobProductsByListId(id)
//                    .catch { e ->
//                        // previously unhandled exception (= not handled at Room level)
//                        // --> catch it here and represent in Resource status
//                        _smobListItems.value = Resource.error(e.toString(), null)
//                        showSnackBar.value = _smobListItems.value.message
//                    }
//                    .collectLatest {
//                        // no exception during flow collection
//                        when(it.status) {
//                            Status.SUCCESS -> {
//                                // --> store successfully received data in StateFlow value
//                                _smobListItems.value = it
//                                updateShowNoData(it)
//                            }
//                            Status.ERROR -> {
//                                // these are errors handled at Room level --> display
//                                showSnackBar.value = it.message
//                                _smobListItems.value = it  // still return Resource value (w/h error)
//                            }
//                            Status.LOADING -> {
//                                // could control visibility of progress bar here
//                            }
//                        }
//                    }
//
//            }  // coroutine
//
//        }  // listId set
//
//    }  // fetchSmobListItems
//
//
//    /**
//     * combine the two flows (products, shopping list [product status]) and turn into StateFlow
//     */
//    @ExperimentalCoroutinesApi
//    fun fetchCombinedFlows() {
//
//        // list ID set yet?
//        currListId?.let { id ->
//
//            val itemsFlow = productDataSource.getSmobProductsByListId(id)
//            val listFlow = listDataSource.getSmobList(id)
//
//            // collect flow
//            viewModelScope.launch {
//
//                itemsFlow.combine(listFlow) { items, list ->
//
//                    // unwrap list (from Resource)
//                    list.data?.let { rawList ->
//
//                        // evaluate/unwrap Resource
//                        when (items.status) {
//
//                            Status.SUCCESS -> {
//                                // received the items on the list alright --> process
//                                items.data?.map { product ->
//
//                                    // at this point, the products on the shopping lists have been properly
//                                    // received --> implies that the list itself is also available
//                                    // output merged data type (with product item status)
//
//                                    // fetch product details from 'items' list on the smobList
//                                    val productOnList =
//                                        rawList.items.first { item -> item.id == product.id }
//
//                                    SmobProductOnListATO(
//                                        id = product.id,
//                                        status = productOnList.status,
//                                        position = productOnList.listPosition,
//                                        productName = product.name,
//                                        productDescription = product.description,
//                                        productImageUrl = product.imageUrl,
//                                        productCategory = product.category,
//                                        productActivity = product.activity,
//                                        listId = rawList.id,
//                                        listName = rawList.name,
//                                        listDescription = rawList.description,
//                                        listItems = rawList.items,
//                                        listGroups = rawList.groups,
//                                        listLifecycle = rawList.lifecycle,
//                                    )
//                                }
//                            }
//                            else -> {
//                                // Status.LOADING or Status.ERROR -> do nothing
//                                null
//                            }
//
//                        }  // when
//
//                    }  // unwrap list
//
//                }  // combine (flows)
//                    .catch { e ->
//                        // previously unhandled exception (= not handled at Room level) --> console
//                        Timber.e(e.toString())
//                    }
//                    .collectLatest {
//                        // store collected flow data in StateFlow value
//                        _smobListItemsWithStatus.value = it
//                    }
//
//            }  // viewModelScope.launch
//
//        }  // listId set
//
//    }  //  combineFlowsAndConvertToStateFlow


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
            smobListItems.take(1).collect {

                if(it.status == Status.ERROR) {
                    showSnackBar.value = it.message!!
                }

                // check if the "no data" symbol has to be shown (empty list)
                updateShowNoData(it)
            }

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
                    (it as Ato).status == ItemStatus.DELETED
                } ||
                // show 'no data', if the currently logged in user is not affiliated with any groups
                SmobApp.currUser?.hasGroupRefs()?.not() ?: false
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

    // default
    // ... using 'postValue' as the VM is instantiated from within a coroutine, when the WorkManager
    //     scheduled geoFence is triggered (and "doWork" is called)
    init {
        selectedShop.postValue(
            SmobShopATO(
                "no product selected yet (id)",
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
        )

        smobProductCategory.postValue(
            ProductCategory(ProductMainCategory.OTHER, ProductSubCategory.OTHER)
        )
    }

    /**
     * Clear the live data objects to start fresh next time the view model gets called
     */
    fun onClearProduct() {
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
            productDataSource.saveSmobItem(smobProductData)
            showLoading.value = false

            // only travel back, if requested
            if(navBack) navigationCommand.value = NavigationCommand.Back

//            // update StateFlow values by re-fetching flows from local DB
//            fetchSmobList()
//            fetchSmobListItems()
//            fetchCombinedFlows()

            // update StateFlow values by re-fetching flows from local DB
            smobListItems.take(1).collect {

                if(it.status == Status.ERROR) {
                    showSnackBar.value = it.message!!
                }

                // check if the "no data" symbol has to be shown (empty list)
                updateShowNoData(it)
            }

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

    // fetch list of smobShops (flow --> StateFlow)
    private val _smobShopList = MutableStateFlow<Resource<List<SmobShopATO?>>>(Resource.loading(null))
    val smobShopList = _smobShopList.asStateFlow()
//    val smobShopList = shopDataSource.getAllSmobShops().asLiveData()


    /**
     * collect the flow of the list of SmobShops
     */
    @ExperimentalCoroutinesApi
    fun fetchSmobShopList() {

        // collect flow
        shopDataSource.getAllSmobItems()
            .catch { e ->
                // previously unhandled exception (= not handled at Room level)
                // --> catch it here and represent in Resource status
                _smobShopList.value = Resource.error(e.toString(), null)
                showSnackBar.value = _smobShopList.value.message ?: "(no message)"
            }
            .take(1)
            .onEach {
                // no exception during flow collection
                when(it.status) {
                    Status.SUCCESS -> {
                        // --> store successfully received data in StateFlow value
                        _smobShopList.value = it
                        updateShowNoData(it)
                    }
                    Status.ERROR -> {
                        // these are errors handled at Room level --> display
                        showSnackBar.value = it.message ?: "(no message)"
                        _smobShopList.value = it  // still return Resource value (w/h error)
                    }
                    Status.LOADING -> {
                        // could control visibility of progress bar here
                    }
                }
            }
            .launchIn(viewModelScope)  // co-routine scope

    }  // fetchSmobShopList



    // fetch an individual smobShop (flow --> StateFlow)
//    private val _smobShop = MutableStateFlow<Resource<SmobShopATO?>>(Resource.loading(null))


    /**
     * update all items in the local DB by querying the backend - triggered on "swipe down"
     */
    @ExperimentalCoroutinesApi
    fun swipeRefreshShopDataInLocalDB() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {

            // update backend DB (from net API)
            shopDataSource.refreshDataInLocalDB()

            // collect flow and update StateFlow values
            fetchSmobShopList()

            // check if the "no data" symbol has to be shown (empty list)
            updateShowNoData(_smobShopList.value)

        }

    }  // swipeRefreshShopDataInLocalDB


//    // list that holds the SmobShop data items to be displayed on the UI
//    // ... flow, converted to StateFlow --> data changes in the backend are observed
//    val smobShopList = shopDataSource.getAllSmobShops().stateIn(
//        scope = viewModelScope,
//        started = WhileSubscribed(5000),
//        initialValue = Resource.loading(null)
//    )
//
//
//    /**
//     * update all items in the local DB by querying the backend - triggered on "swipe down"
//     */
//    @ExperimentalCoroutinesApi
//    fun swipeRefreshShopDataInLocalDB() {
//
//        // user is impatient - trigger update of local DB from net
//        viewModelScope.launch {
//
//            // update backend DB (from net API)
//            shopDataSource.refreshDataInLocalDB()
//
//            smobShopList.collectLatest {
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
//    }  // swipeRefreshShopDataInLocalDB

    /**
     * Save the smob product to the data source
     */
    fun saveSmobListItem(smobListData: SmobListATO, goBack: Boolean) {
        showLoading.value = true
        viewModelScope.launch {
            listDataSource.saveSmobItem(smobListData)
            showLoading.value = false

            showToast.value = app.getString(R.string.smob_list_saved)

            // only travel back, if requested
            if(goBack) navigationCommand.value = NavigationCommand.Back
        }
    }


    // (ex)-PlanningListsViewModel --------------------------------------------
    // (ex)-PlanningListsViewModel --------------------------------------------
    // (ex)-PlanningListsViewModel --------------------------------------------


    // list that holds the smob data items to be displayed on the UI
    // ... flow, converted to StateFlow --> data changes in the backend are observed
    // ... ref: https://medium.com/androiddevelopers/migrating-from-livedata-to-kotlins-flow-379292f419fb
    private val _smobLists = MutableStateFlow<Resource<List<SmobListATO?>>>(Resource.loading(null))
    val smobLists = _smobLists.asStateFlow()

    // Detailed investigation of Flow vs. LiveData: LD seems the better fit for UI layer
    // see: https://bladecoder.medium.com/kotlins-flow-in-viewmodels-it-s-complicated-556b472e281a
    //
    // --> reverting back to LiveData at ViewModel layer (collection point) and benefiting of the
    //     much less cumbersome handling of the data incl. the better "lifecycle optimized" behavior
    //
    // --> using ".asLiveData()", as the incoming flow is not based on "suspendable" operations
    //     (already handled internally at Room level --> no "suspend fun" for read operations, see
    //     DAO)
    // --> alternative (with Coroutine scope would be to use ... = liveData { ... suspend fun ... })
//    val smobLists = listsDataSource.getAllSmobLists().asLiveData()


    /**
     * collect the flow of the upstream list the user just selected
     */
    @ExperimentalCoroutinesApi
    fun fetchSmobLists() {

        // collect flow
        listDataSource.getAllSmobItems()
            .catch { e ->
                // previously unhandled exception (= not handled at Room level)
                // --> catch it here and represent in Resource status
                _smobLists.value = Resource.error(e.toString(), null)
                showSnackBar.value = _smobLists.value.message ?: "(no message)"
            }
            .take(1)
            .onEach {
                // no exception during flow collection
                when(it.status) {
                    Status.SUCCESS -> {
                        // --> store successfully received data in StateFlow value
                        _smobLists.value = it
                        updateShowNoData(it)
                    }
                    Status.ERROR -> {
                        // these are errors handled at Room level --> display
                        showSnackBar.value = it.message ?: "(no message)"
                        _smobLists.value = it  // still return Resource value (w/h error)
                    }
                    Status.LOADING -> {
                        // could control visibility of progress bar here
                    }
                }
            }
            .launchIn(viewModelScope)  // co-routine scope

    }  // fetchSmobLists


    /**
     * update all items in the local DB by querying the backend - triggered on "swipe down"
     */
    @ExperimentalCoroutinesApi
    fun swipeRefreshDataInLocalDB() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {

            // update backend DB (from net API)
            listDataSource.refreshDataInLocalDB()

            // load SmobLists from local DB and store in StateFlow value
            fetchSmobLists()

            // check if the "no data" symbol has to be shown (empty list)
            updateShowNoData(_smobLists.value)

        }

    }  // swipeRefreshDataInLocalDB



    // (ex)-PlanningListEditViewModel ---------------------------------------------
    // (ex)-PlanningListEditViewModel ---------------------------------------------
    // (ex)-PlanningListEditViewModel ---------------------------------------------

    val smobListName = MutableLiveData<String?>()
    val smobListDescription = MutableLiveData<String?>()
    private val smobListImageUrl = MutableLiveData<String?>()

    init {
        onClearList()
    }

    /**
     * Clear the live data objects to start fresh next time the view model gets instantiated
     */
    private fun onClearList() {
        // viewModel is initiated in background "doWork" job (coroutine)
        smobListName.postValue(null)
        smobListDescription.postValue(null)
        smobListImageUrl.postValue(null)
    }

    /**
     * Validate the entered data then saves the smobList to the DataSource
     */
    fun validateAndSaveSmobList(shopMobData: SmobListATO) {
        if (validateEnteredData(shopMobData)) {
            saveSmobListItem(shopMobData)
            navigationCommand.value = NavigationCommand.Back
        }
    }

    /**
     * Save the smobList item to the data source
     */
    private fun saveSmobListItem(smobListData: SmobListATO) {
        showLoading.value = true
        viewModelScope.launch {
            // store in local DB (and sync to server)
            listDataSource.saveSmobItem(smobListData)
        }
        showLoading.value = false

        // load SmobLists from local DB to update StateFlow value
        fetchSmobLists()

        // check if the "no data" symbol has to be shown (empty list)
        updateShowNoData(_smobLists.value)
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
    private fun validateEnteredData(shopMobData: SmobListATO): Boolean {

        if (shopMobData.name.isEmpty()) {
            showSnackBarInt.value = R.string.err_enter_title
            return false
        }

        // successful validation
        return true
    }


}
