package com.tanfra.shopmob.smob.activities.planning.productList

import android.app.Application
import androidx.lifecycle.*
import com.tanfra.shopmob.base.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
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
    private val listRepoFlow: SmobListDataSource,
    private val productRepoFlow: SmobProductDataSource
) : BaseViewModel(app) {

    // selected item from the upstream list
    // ... re-fetched during creation of the fragment to allow for establishment of a live
    //     connection to the local DB
    lateinit var upstreamListItem: Flow<SmobListATO>

    /**
     * fetch the item of the upstream list the user just selected
     */
    @ExperimentalCoroutinesApi
    fun fetchUpstreamListItem(id: String): Flow<SmobListATO> {
        val fetchFlow = listRepoFlow.getSmobList(id)
        return fetchFlow.transformLatest { value -> emit(value.data) }.filterNotNull()
    }

    /**
     * fetch the item of the upstream list the user just selected
     */
    @ExperimentalCoroutinesApi
    fun fetchListItems(id: String): StateFlow<Resource<List<SmobProductATO>>> {
        val fetchFlow = productRepoFlow.getSmobProductsByListId(id)
        return fetchFlow.stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5000),
            initialValue = Resource.loading(null)
        )  // StateFlow<...>
    }

    // collect list of SmobProductATO items in StateFlow variable
    // ... lateinit, as this can only be done once the fragment is created (and the id's are here)
    lateinit var smobList: StateFlow<Resource<List<SmobProductATO>>>

//    // create flow of list that holds the smob data items to be displayed on the UI
//    // ... flow, converted to StateFlow --> data changes in the backend are observed
//    // ... ref: https://medium.com/androiddevelopers/migrating-from-livedata-to-kotlins-flow-379292f419fb
//    //
//    @ExperimentalCoroutinesApi
//    fun fetchListItems(upstreamList: Flow<SmobListATO>): StateFlow<Resource<List<SmobProductATO>>> {
//
//        // transform flow
//        val listOfIds = upstreamList
//            .map { list -> list.items }  // Flow<List<SmobListItem>>
//            .map { itemsList -> itemsList.map { item -> item.id } }  // Flow<List<String>>
//
//        // loop over all IDs within the flow (of ID strings)
//        return listOfIds.transformLatest { idL ->
//
//            // assemble product list
//            val chosenProducts = mutableListOf<SmobProductATO>()
//
//            // indicate that we're loading data
//            emit(Resource.loading(null))
//
//            // fetch all items on the product ID list (idL)
//            idL.map { id ->
//
//                // fetch next product item - this is where we collect the flow
//                productRepoFlow.getSmobProduct(id).map {
//
//                    // only add successfully received products
//                    if (it.status == Status.SUCCESS) {
//                        it.data?.let { product -> chosenProducts.add(product) }
//                    }
//
//                }  // flow: collect
//
//                // send the list of products on the selected smob list
//                emit(Resource.success(chosenProducts))
//
//            }  // all IDs on the product id list
//
//        }.stateIn(
//            scope = viewModelScope,
//            started = WhileSubscribed(5000),
//            initialValue = Resource.loading(null)
//        )  // StateFlow<...>
//
//    }  // fetchListItems


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
            smobList.collect {
                if(it.status == Status.ERROR) showSnackBar.value = it.message!!
            }
            // check if no data has to be shown
            updateShowNoData()

        }

    }  // swipeRefreshDataInLocalDB

    /**
     * Inform the user that the list is empty
     */
    @ExperimentalCoroutinesApi
    private suspend fun updateShowNoData() {
        val smobListNewest = smobList.last()
        showNoData.value = (smobListNewest.status == Status.SUCCESS && smobListNewest.data!!.isEmpty())
    }

}


