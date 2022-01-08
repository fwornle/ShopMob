package com.tanfra.shopmob.smob.activities.planning.productList

import android.app.Application
import androidx.lifecycle.*
import com.tanfra.shopmob.base.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductOnListATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobListDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.launch

class PlanningProductListViewModel(
    app: Application,
    val listRepoFlow: SmobListDataSource,  // public, as used (externally) to update the smobList
    private val productRepoFlow: SmobProductDataSource
) : BaseViewModel(app) {

    // collect the upstream selected smobList as well as the list of SmobProductATO items
    // ... lateinit, as this can only be done once the fragment is created (and the id's are here)
    lateinit var _smobList: Flow<Resource<SmobListATO?>>
    lateinit var _smobListItems: Flow<Resource<List<SmobProductATO>?>>
    lateinit var smobList: StateFlow<Resource<SmobListATO?>>
    lateinit var smobListItems: StateFlow<Resource<List<SmobProductATO>?>>
    lateinit var smobListItemsWithStatus: StateFlow<List<SmobProductOnListATO>?>

    /**
     * fetch the flow of the upstream list the user just selected
     */
    @ExperimentalCoroutinesApi
    fun fetchSmobListFlow(id: String): Flow<Resource<SmobListATO?>> {
        val fetchFlow = listRepoFlow.getSmobList(id)
        return fetchFlow
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
        val fetchFlow = productRepoFlow.getSmobProductsByListId(id)
        return fetchFlow
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
    ): StateFlow<List<SmobProductOnListATO>?> {

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
                            SmobProductOnListATO(
                                id = product.id,
                                productName = product.name,
                                productDescription = product.description,
                                productImageUrl = product.imageUrl,
                                productCategory = product.category,
                                productActivity = product.activity,
                                listItemStatus = rawList.items.first { item -> item.id == product.id }.status,
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

        }  // combine(Flow_1, Flow_2)
            .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5000),
            initialValue = listOf()
        )  // StateFlow<...>

    }  //  combineFlowsAndConvertToStateFlow



    /**
     * update all items in the local DB by querying the backend - triggered on "swipe down"
     */
    @ExperimentalCoroutinesApi
    fun swipeRefreshDataInLocalDB() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {

            // update backend DB (from net API)
            productRepoFlow.refreshDataInLocalDB()

            // handle potential errors
            smobListItems.collect {

                if(it.status == Status.ERROR) {
                    showSnackBar.value = it.message!!
                }

                // check if the "no data" symbol has to be shown (empty list)
                updateShowNoData(it)
            }

        }

    }  // swipeRefreshDataInLocalDB

    /**
     * Inform the user that the list is empty
     */
    @ExperimentalCoroutinesApi
    private fun updateShowNoData(smobListNewest: Resource<List<*>?>) {
        showNoData.value = (smobListNewest.status == Status.SUCCESS && smobListNewest.data!!.isEmpty())
    }

}
