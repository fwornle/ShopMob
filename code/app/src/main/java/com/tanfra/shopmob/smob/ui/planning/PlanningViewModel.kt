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
import com.tanfra.shopmob.smob.data.remote.utils.NetworkConnectionManager
import com.tanfra.shopmob.smob.data.repo.ato.*
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobListRepository
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobProductRepository
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobShopRepository
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationCommand
import com.tanfra.shopmob.smob.ui.planning.lists.PlanningListsViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class PlanningViewModel(
    val app: Application,
    val listRepository: SmobListRepository,  // public, as used (externally) to update the smobList
    private val productRepository: SmobProductRepository,
    val shopRepository: SmobShopRepository,
    ) : BaseViewModel(app), KoinComponent {

    // fetch worker class form service locator
    val networkConnectionManager: NetworkConnectionManager by inject()

    // navigation source - used to be able to have a "modal" list item click handler
    // ... depending on how we got to the list (and, as such, on how we want to use it)
    var navSource = "navDrawer"  // default

    // current list ID and list position (in the list of SmobLists)
    var currListId: String? = null

    // define PlanningLists UI state as flow
    private val viewStateListsMSF = MutableStateFlow(PlanningListsViewState())
    val viewStateListsSF = viewStateListsMSF.asStateFlow()



    // collect the upstream selected smobList as well as the list of SmobProductATO items
    // ... lateinit, as this can only be done once the fragment is created (and the id's are here)
    lateinit var smobListF: Flow<Resource<SmobListATO>>
    lateinit var smobListSF: StateFlow<Resource<SmobListATO>>

    lateinit var smobListProductsF: Flow<Resource<List<SmobProductATO>>>
    lateinit var smobListProductsSF: StateFlow<Resource<List<SmobProductATO>>>
    lateinit var smobListProductsWithListDataSF: StateFlow<List<SmobProductWithListDataATO>>


    /**
     * fetch the flow of the upstream list the user just selected
     */
    @ExperimentalCoroutinesApi
    fun getFlowOfSmobList(id: String): Flow<Resource<SmobListATO>> {
        return listRepository.getSmobItem(id)
    }

    // convert to StateFlow
    fun smobListFlowToStateFlow(inFlow: Flow<Resource<SmobListATO>>): StateFlow<Resource<SmobListATO>> {
        return inFlow.stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5000),
            initialValue = Resource.Loading
        )
    }

    /**
     * fetch the flow of the list of products for the upstream SmobList the user just selected
     */
    @ExperimentalCoroutinesApi
    fun getFlowOfSmobListProducts(id: String): Flow<Resource<List<SmobProductATO>>> {
        return productRepository.getSmobProductsByListId(id)
    }

    // convert to StateFlow
    fun smobListProductsFlowToStateFlow(inFlow: Flow<Resource<List<SmobProductATO>>>): StateFlow<Resource<List<SmobProductATO>>> {
        return inFlow.stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5000),
            initialValue = Resource.Loading
        )
    }

    /**
     * combine the two flows (products, shopping list [product status]) and turn into StateFlow
     */
    @ExperimentalCoroutinesApi
    fun combineFlowsAndConvertToStateFlow(
        listFlow: Flow<Resource<SmobListATO>>,
        itemsFlow: Flow<Resource<List<SmobProductATO>>>,
    ): StateFlow<List<SmobProductWithListDataATO>> {

        return itemsFlow.combine(listFlow) { items, list ->

            // unwrap list (from Resource)
            when (list) {
                is Resource.Error -> {
                    Timber.i("Couldn't retrieve SmobList from remote")
                    listOf()
                }
                is Resource.Loading -> {
                    Timber.i("SmobList still loading")
                    listOf()
                }
                is Resource.Success -> {
                    list.data.let { rawList ->

                        // evaluate/unwrap Resource
                        when (items) {
                            is Resource.Error -> {
                                Timber.i("Couldn't retrieve SmobProducts from remote")
                                listOf()
                            }
                            is Resource.Loading -> {
                                Timber.i("SmobProducts still loading")
                                listOf()
                            }
                            is Resource.Success -> {

                                // received the items on the list alright --> process
                                items.data.map { product ->

                                    // at this point, the products on the shopping lists have been properly
                                    // received --> implies that the list itself is also available
                                    // output merged data type (with product item status)

                                    // fetch product details from 'items' list on the smobList
                                    val productOnList =
                                        rawList.items.first { item -> item.id == product.id }

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

                                }  // let...
                            }  // Resource.Success
                        }  // when(items)

                    }  // let...
                }  // Resource.Success
            }  // when(list)

        }  // combine(Flow_1, Flow_2)
            .stateIn(
                scope = viewModelScope,
                started = WhileSubscribed(5000),
                initialValue = listOf()
            )

    }  //  combineFlowsAndConvertToStateFlow



    /**
     * update all items in the local DB by querying the backend - triggered on "swipe down"
     */
    @ExperimentalCoroutinesApi
    fun swipeRefreshProductDataInLocalDB() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {

            // update backend DB (from net API)
            productRepository.refreshItemsInLocalDB()

            // collect flow to update StateFlow with current value from DB
            smobListProductsSF.take(1).collect {

                when (it) {
                    is Resource.Error -> { showSnackBar.value = it.exception.message }
                    is Resource.Loading -> throw(Exception("SmobProducts still loading"))
                    is Resource.Success -> updateShowNoData(it)
                }

            }

        }

    }  // swipeRefreshProductDataInLocalDB

    /**
     * Inform the user that the list is empty
     */
    @ExperimentalCoroutinesApi
    private fun updateShowNoData(smobListNewest: Resource<List<*>>) {
        showNoData.value = when(smobListNewest) {
            is Resource.Success -> {
                smobListNewest.data.isEmpty() ||
                smobListNewest.data.all { (it as Ato).status == ItemStatus.DELETED } ||
                SmobApp.currUser?.hasGroupRefs()?.not() ?: false
            }
            else -> false
        }
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
                ItemStatus.INVALID,
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
            saveSmobProductItem(shopMobData)
        }
    }

    /**
     * Save the smob product to the data source
     */
    private fun saveSmobProductItem(smobProductData: SmobProductATO) {
        showLoading.value = true
        viewModelScope.launch {
            productRepository.saveSmobItem(smobProductData)
            showLoading.value = false

            // travel back
            navigationCommand.value = NavigationCommand.Back

            // update StateFlow values by re-collecting flows from local DB
            smobListProductsSF.take(1).collect {

                when (it) {
                    is Resource.Error -> { showSnackBar.value = it.exception.message }
                    is Resource.Loading -> throw(Exception("SmobProducts still loading"))
                    is Resource.Success -> updateShowNoData(it)
                }

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

    // static StateFlows (independent of user choice / id)
    val smobShopListSF = shopRepository.getSmobItems()
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5000),
            initialValue = Resource.Loading
        )


    /**
     * collect the flow of the list of SmobShops
     */
    @ExperimentalCoroutinesApi
    private fun collectSmobShopList() {

        viewModelScope.launch {

            // collect flow / update SF
            smobShopListSF
                .take(1)
                .collect {
                when (it) {
                    is Resource.Loading -> Timber.i("SmobShops still loading")
                    is Resource.Error -> {
                        // these are errors handled at Room level --> display
                        showSnackBar.value = it.exception.message ?: "(no message)"
                    }
                    is Resource.Success -> {
                        // --> turn empty list into
                        updateShowNoData(it)
                    }
                }
            }

        }

    }  // collectSmobShopList


    /**
     * update all items in the local DB by querying the backend - triggered on "swipe down"
     */
    @ExperimentalCoroutinesApi
    fun swipeRefreshShopDataInLocalDB() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {

            // update backend DB (from net API)
            shopRepository.refreshItemsInLocalDB()

            // collect flow and update StateFlow values (to get it out of the initial loading state)
            collectSmobShopList()

        }

    }  // swipeRefreshShopDataInLocalDB



    /**
     * Save the smob product to the data source
     */
    fun saveSmobListItem(smobListData: SmobListATO, goBack: Boolean) {
        showLoading.value = true
        viewModelScope.launch {
            listRepository.saveSmobItem(smobListData)
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
    private val smobListsStaticMSF = MutableStateFlow<Resource<List<SmobListATO>>>(Resource.Loading)
    val smobListsStaticSF = smobListsStaticMSF.asStateFlow()

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
    private fun collectSmobLists() {

        // collect flow
        listRepository.getSmobItems()
            .catch { ex ->
                // previously unhandled exception (= not handled at Room level)
                // --> catch it here and represent in Resource status
                smobListsStaticMSF.value = Resource.Error(Exception(ex))
                showSnackBar.value = ex.message ?: "(no message)"
            }
            .take(1)
            .onEach {
                // no exception during flow collection
                when (it) {
                    is Resource.Loading -> throw(Exception("SmobProducts still loading"))
                    is Resource.Error ->{
                        // these are errors handled at Room level --> display
                        showSnackBar.value = it.exception.message ?: "(no message)"
                        smobListsStaticMSF.value = it  // still return Resource value (w/h error)
                    }
                    is Resource.Success -> {
                        // --> store successfully received data in StateFlow value
                        smobListsStaticMSF.value = it
                        updateShowNoData(it)
                    }
                }
            }
            .launchIn(viewModelScope)  // co-routine scope

    }  // collectSmobLists


    /**
     * update all items in the local DB by querying the backend - triggered on "swipe down"
     */
    @ExperimentalCoroutinesApi
    fun swiperefreshItemsInLocalDB() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {

            // update backend DB (from net API)
            listRepository.refreshItemsInLocalDB()

            // load SmobLists from local DB and store in StateFlow value
            collectSmobLists()

            // check if the "no data" symbol has to be shown (empty list)
            updateShowNoData(smobListsStaticMSF.value)

        }

    }  // swiperefreshItemsInLocalDB



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
            listRepository.saveSmobItem(smobListData)
        }
        showLoading.value = false

        // load SmobLists from local DB to update StateFlow value
        collectSmobLists()

        // check if the "no data" symbol has to be shown (empty list)
        updateShowNoData(smobListsStaticMSF.value)
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
