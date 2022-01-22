package com.tanfra.shopmob.smob.ui.planning.lists

import android.app.Application
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.ui.base.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobListDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class PlanningListsViewModel(
    app: Application,
    val listsDataSource: SmobListDataSource,
) : BaseViewModel(app) {

    // list that holds the smob data items to be displayed on the UI
    // ... flow, converted to StateFlow --> data changes in the backend are observed
    // ... ref: https://medium.com/androiddevelopers/migrating-from-livedata-to-kotlins-flow-379292f419fb
    private val _smobLists = MutableStateFlow<Resource<List<SmobListATO?>>>(Resource.loading(null))

    // public getter for SmobLists
    fun getSmobLists(): StateFlow<Resource<List<SmobListATO?>>> = _smobLists


    /**
     * collect the flow of the upstream list the user just selected
     */
    @ExperimentalCoroutinesApi
    fun fetchSmobLists() {

        // collect flow
        viewModelScope.launch {

            // flow terminator
            listsDataSource.getAllSmobLists()
                .catch { e ->
                    // previously unhandled exception (= not handled at Room level)
                    // --> catch it here and represent in Resource status
                    _smobLists.value = Resource.error(e.toString(), null)
                    showSnackBar.value = _smobLists.value.message
                }
                .collect {
                    // no exception during flow collection
                    when(it.status) {
                        Status.SUCCESS -> {
                            // --> store successfully received data in StateFlow value
                            _smobLists.value = it
                            updateShowNoData(it)
                        }
                        Status.ERROR -> {
                            // these are errors handled at Room level --> display
                            showSnackBar.value = it.message
                            _smobLists.value = it  // still return Resource value (w/h error)
                        }
                        Status.LOADING -> {
                            // could control visibility of progress bar here
                        }
                    }
                }

        }  // coroutine

    }  // fetchSmobLists


    // Detailed investigation of Flow vs. LiveData: LD seems the better fit for UI layer
    // see: https://bladecoder.medium.com/kotlins-flow-in-viewmodels-it-s-complicated-556b472e281a
    //
    // --> reverting back to LiveData at ViewModel layer (collection point) and profiting of the
    //     much less cumbersome handling of the data incl. the better "lifecycle optimized" behavior
    //
    // --> using ".asLiveData()", as the incoming flow is not based on "suspendable" operations
    //     (already handled internally at Room level --> no "suspend fun" for read operations, see
    //     DAO)
    // --> alternative (with Coroutine scope would be to use ... = liveData { ... suspend fun ... })
//    val smobLists = listsDataSource.getAllSmobLists().asLiveData()

    /**
     * update all items in the local DB by querying the backend - triggered on "swipe down"
     */
    @ExperimentalCoroutinesApi
    fun swipeRefreshDataInLocalDB() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {

            // update backend DB (from net API)
            listsDataSource.refreshDataInLocalDB()

            // load SmobLists from local DB and store in StateFlow value
            fetchSmobLists()

            // check if the "no data" symbol has to be shown (empty list)
            updateShowNoData(_smobLists.value)

        }

    }  // swipeRefreshDataInLocalDB


    /**
     * Inform the user that the list is empty
     */
    @ExperimentalCoroutinesApi
    private fun updateShowNoData(smobListNewest: Resource<List<*>>) {
        showNoData.value = (
                smobListNewest.status == Status.SUCCESS && smobListNewest.data!!.isEmpty() ||
                smobListNewest.status == Status.SUCCESS && smobListNewest.data!!.all {
                    (it as Ato).itemStatus == SmobItemStatus.DELETED
                }
                )
    }


    // (ex)-PlanningListEditViewModel ---------------------------------------------
    // (ex)-PlanningListEditViewModel ---------------------------------------------
    // (ex)-PlanningListEditViewModel ---------------------------------------------

    val smobListName = MutableLiveData<String?>()
    val smobListDescription = MutableLiveData<String?>()
    val smobListImageUrl = MutableLiveData<String?>()

    init {
        onClear()
    }

    /**
     * Clear the live data objects to start fresh next time the view model gets instantiated
     */
    fun onClear() {
        smobListName.value = null
        smobListDescription.value = null
        smobListImageUrl.value = null
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
            listsDataSource.saveSmobList(smobListData)
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