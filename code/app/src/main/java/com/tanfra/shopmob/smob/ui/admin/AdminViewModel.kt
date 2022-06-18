package com.tanfra.shopmob.smob.ui.admin

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.data.repo.ato.*
import com.tanfra.shopmob.smob.ui.base.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobGroupDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobProductDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobUserDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class AdminViewModel(
    app: Application,
    val groupDataSource: SmobGroupDataSource,  // public - also used in AdminGroupsAdapter
    private val userDataSource: SmobUserDataSource,
    ) : BaseViewModel(app) {

//    // current list ID and list position (in the list of SmobGroups)
//    var currListId: String? = null
//
//    // collect the upstream selected smobGroupList as well as the list of SmobUserATO items
//    // ... lateinit, as this can only be done once the fragment is created (and the id's are here)
//    lateinit var _smobGroupList: Flow<Resource<List<SmobGroupATO?>>>
//    lateinit var smobGroupList: StateFlow<Resource<List<SmobGroupATO>>>
//
//    val _smobGroupList2 = MutableStateFlow<Resource<List<SmobGroupATO?>>>(Resource.loading(null))
//    val smobGroupList2 = _smobGroupList2.asStateFlow()
//
//    lateinit var _smobGroupMembers: Flow<Resource<List<SmobUserATO>?>>
//    lateinit var smobGroupMembers: StateFlow<Resource<List<SmobUserATO>?>>
//
//
//    /**
//     * fetch the flow of the upstream list the user just selected
//     */
//    @ExperimentalCoroutinesApi
//    fun fetchSmobGroupListFlow(): Flow<Resource<List<SmobGroupATO>>> =
//        groupDataSource.getAllSmobItems()
//
//    // convert to StateFlow
//    fun smobGroupListFlowToStateFlow(
//        inFlow: Flow<Resource<List<SmobGroupATO?>>>
//    ): StateFlow<Resource<List<SmobGroupATO?>>> =
//        inFlow.stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = Resource.loading(null)
//        )  // StateFlow<...>
//
//    /**
//     * fetch the flow of the list of items for the upstream list the user just selected
//     */
//    @ExperimentalCoroutinesApi
//    fun fetchSmobGroupMemberFlow(id: String): Flow<Resource<SmobUserATO?>> =
//        userDataSource.getSmobItem(id)
//
//    // convert to StateFlow
//    fun smobGroupMemberFlowToStateFlow(
//        inFlow: Flow<Resource<SmobUserATO?>>
//    ): StateFlow<Resource<SmobUserATO?>> =
//        inFlow.stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = Resource.loading(null)
//        )  // StateFlow<...>
//
//
//    /**
//     * collect the flow of the upstream list the user just selected
//     */
//    @ExperimentalCoroutinesApi
//    fun fetchSmobGroup() {
//
//        // list ID set yet?
//        currListId?.let { id ->
//
//            // collect flow
//            viewModelScope.launch {
//
//                // flow terminator
//                groupDataSource.getSmobItem(id)
//                    .catch { e ->
//                        // previously unhandled exception (= not handled at Room level)
//                        // --> catch it here and represent in Resource status
//                        _smobGroupList2.value = Resource.error(e.toString(), null)
//                        showSnackBar.value = _smobGroupList2.value.message
//                    }
//                    .collectLatest {
//                        // no exception during flow collection
//                        when(it.status) {
//                            Status.SUCCESS -> {
//                                // --> store successfully received data in StateFlow value
//                                // TODO : currently just fudged to get it to compile
//                                // TODO : currently just fudged to get it to compile
//                                // TODO : currently just fudged to get it to compile
//                                _smobGroupList2.value = Resource(Status.SUCCESS, listOf(it.data), it.message)
//                            }
//                            Status.ERROR -> {
//                                // these are errors handled at Room level --> display
//                                showSnackBar.value = it.message
//                                _smobGroupList2.value =
//                                        // TODO : currently just fudged to get it to compile
//                                        // TODO : currently just fudged to get it to compile
//                                        // TODO : currently just fudged to get it to compile
//                                    Resource(Status.ERROR, listOf(it.data), it.message) // still return Resource value (w/h error)
//                            }
//                            Status.LOADING -> {
//                                // could control visibility of progress bar here
//                            }
//                        }
//                    }
//
//            }  // coroutine
//
//        } // listId set
//
//    }  // fetchSmobGroup
//
//
//    /**
//     * update all items in the local DB by querying the backend - triggered on "swipe down"
//     */
//    @ExperimentalCoroutinesApi
//    fun swipeRefreshGroupsDataInLocalDB() {
//
//        // user is impatient - trigger update of local DB from net
//        viewModelScope.launch {
//
//            // update backend DB (from net API)
//            userDataSource.refreshDataInLocalDB()
//
//            // collect flow to update StateFlow with current value from DB
//            smobGroupMembers.take(1).collect {
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
//    }  // swipeRefreshProductDataInLocalDB



    // GroupsListViewModel --------------------------------------------
    // GroupsListViewModel --------------------------------------------
    // GroupsListViewModel --------------------------------------------


    // list that holds the smob data items to be displayed on the UI
    // ... flow, converted to StateFlow --> data changes in the backend are observed
    // ... ref: https://medium.com/androiddevelopers/migrating-from-livedata-to-kotlins-flow-379292f419fb
    private val _smobGroups = MutableStateFlow<Resource<List<SmobGroupATO>>>(Resource.loading(null))
    val smobGroups = _smobGroups.asStateFlow()

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
//    val smobGroups = groupDataSource.getAllSmobGroups().asLiveData()


    /**
     * collect the flow of the upstream list the user just selected
     */
    @ExperimentalCoroutinesApi
    fun fetchsmobGroups() {

        // collect flow
        viewModelScope.launch {

            // flow terminator
            groupDataSource.getAllSmobItems()
                .catch { e ->
                    // previously unhandled exception (= not handled at Room level)
                    // --> catch it here and represent in Resource status
                    _smobGroups.value = Resource.error(e.toString(), null)
                    showSnackBar.value = _smobGroups.value.message
                }
                .take(1)
                .collect {
                    // no exception during flow collection
                    when(it.status) {
                        Status.SUCCESS -> {
                            // --> store successfully received data in StateFlow value
                            _smobGroups.value = it
                            updateShowNoData(it)
                        }
                        Status.ERROR -> {
                            // these are errors handled at Room level --> display
                            showSnackBar.value = it.message
                            _smobGroups.value = it  // still return Resource value (w/h error)
                        }
                        Status.LOADING -> {
                            // could control visibility of progress bar here
                        }
                    }
                }

        }  // coroutine

    }  // fetchsmobGroups


    /**
     * update all items in the local DB by querying the backend - triggered on "swipe down"
     */
    @ExperimentalCoroutinesApi
    fun swipeRefreshDataInLocalDB() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {

            // update backend DB (from net API)
            groupDataSource.refreshDataInLocalDB()

            // load smobGroups from local DB and store in StateFlow value
            fetchsmobGroups()

            // check if the "no data" symbol has to be shown (empty list)
            updateShowNoData(_smobGroups.value)

        }

    }  // swipeRefreshDataInLocalDB



    /**
     * Inform the user that the list is empty
     */
    @ExperimentalCoroutinesApi
    private fun updateShowNoData(smobGroupListNewest: Resource<List<*>?>) {
        showNoData.value = (
                smobGroupListNewest.status == Status.SUCCESS && smobGroupListNewest.data!!.isEmpty() ||
                        smobGroupListNewest.status == Status.SUCCESS && smobGroupListNewest.data!!.all {
                    (it as Ato).itemStatus == SmobItemStatus.DELETED
                }
                )
    }

}