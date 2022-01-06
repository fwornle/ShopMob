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

    // collect list of SmobProductATO items in StateFlow variable
    // ... lateinit, as this can only be done once the fragment is created (and the id's are here)
    lateinit var smobList: StateFlow<Resource<List<SmobProductATO>>>

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
    private fun updateShowNoData(smobListNewest: Resource<List<*>>) {
        showNoData.value = (smobListNewest.status == Status.SUCCESS && smobListNewest.data!!.isEmpty())
    }

}


