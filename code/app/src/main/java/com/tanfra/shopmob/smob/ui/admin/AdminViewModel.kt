package com.tanfra.shopmob.smob.ui.admin

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.local.utils.GroupType
import com.tanfra.shopmob.smob.data.local.utils.SmobItemStatus
import com.tanfra.shopmob.smob.data.repo.ato.*
import com.tanfra.shopmob.smob.ui.base.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobGroupDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobListDataSource
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobUserDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class AdminViewModel(
    app: Application,
    val groupDataSource: SmobGroupDataSource,  // public - used in AdminGroupsAdapter
    val listDataSource: SmobListDataSource,    // public - used in AdminListsAdapter
    private val userDataSource: SmobUserDataSource,
    ) : BaseViewModel(app) {

    
    // AdminGroupsViewModel --------------------------------------------
    // AdminGroupsViewModel --------------------------------------------
    // AdminGroupsViewModel --------------------------------------------


    // list that holds the smob data items to be displayed on the UI
    // ... flow, converted to StateFlow --> data changes in the backend are observed
    // ... ref: https://medium.com/androiddevelopers/migrating-from-livedata-to-kotlins-flow-379292f419fb
    private val _smobGroupsSF = MutableStateFlow<Resource<List<SmobGroupATO>>>(Resource.loading(null))
    val smobGroupsSF = _smobGroupsSF.asStateFlow()  // read-only

    // Detailed investigation of Flow vs. LiveData: LD seems the better fit for UI layer
    // see: https://bladecoder.medium.com/kotlins-flow-in-viewmodels-it-s-complicated-556b472e281a
    //
    // --> reverting back to LiveData at ViewModel layer (collection point) and benefitting of the
    //     much less cumbersome handling of the data incl. the better "lifecycle optimized" behavior
    //
    // --> using ".asLiveData()", as the incoming flow is not based on "suspendable" operations
    //     (already handled internally at Room level --> no "suspend fun" for read operations, see
    //     DAO)
    // --> alternative (with Coroutine scope would be to use ... = liveData { ... suspend fun ... })
//    val smobGroupsLD = groupDataSource.getAllSmobGroups().asLiveData()


    // collect flow of all groups and return as StateFlow (SF)
    @ExperimentalCoroutinesApi
    fun collectAllSmobGroupsSF() {

        // collect flow
        groupDataSource.getAllSmobItems()
            .catch { e ->
                // previously unhandled exception (= not handled at Room level)
                // --> catch it here and represent in Resource status
                _smobGroupsSF.value = Resource.error(e.toString(), null)
                showSnackBar.value = _smobGroupsSF.value.message
            }
            .take(1)
            .onEach {
                // no exception during flow collection
                when(it.status) {
                    Status.SUCCESS -> {
                        // --> store successfully received data in StateFlow value
                        _smobGroupsSF.value = it
                        updateShowNoSmobItemsData(it)
                    }
                    Status.ERROR -> {
                        // these are errors handled at Room level --> display
                        showSnackBar.value = it.message
                        _smobGroupsSF.value = it  // still return Resource value (w/h error)
                    }
                    Status.LOADING -> {
                        // could control visibility of progress bar here
                    }
                }
            }
            .launchIn(viewModelScope)  // co-routine scope

    }  // collectAllSmobGroupsSF


    // update all group items in the local DB by querying the backend - triggered on "swipe down"
    @ExperimentalCoroutinesApi
    fun swipeRefreshGroupDataInLocalDB() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {

            // update backend DB (from net API)
            groupDataSource.refreshDataInLocalDB()

            // load smobGroups from local DB and store in StateFlow value
            collectAllSmobGroupsSF()

            // check if the "no data" symbol has to be shown (empty list)
            updateShowNoSmobItemsData(_smobGroupsSF.value)

        }

    }  // swipeRefreshGroupDataInLocalDB

    // inform the user that the list of SmobItems is empty
    @ExperimentalCoroutinesApi
    private fun updateShowNoSmobItemsData(smobItemsNewest: Resource<List<*>?>) {
        showNoData.value = (
            smobItemsNewest.status == Status.SUCCESS && smobItemsNewest.data!!.isEmpty() ||
                    smobItemsNewest.status == Status.SUCCESS && smobItemsNewest.data!!.all {
                    (it as Ato).itemStatus == SmobItemStatus.DELETED
                }
            )
    }


    // AdminGroupsEditViewModel ---------------------------------------------
    // AdminGroupsEditViewModel ---------------------------------------------
    // AdminGroupsEditViewModel ---------------------------------------------

    val smobGroupName = MutableLiveData<String?>()
    val smobGroupDescription = MutableLiveData<String?>()
    val smobGroupType = MutableLiveData<GroupType?>()

    init {
        onClearGroup()
    }

    // clear the live data objects to start fresh next time the view model gets instantiated
    fun onClearGroup() {
        // viewModel is initiated in background "doWork" job (coroutine)
        smobGroupName.postValue(null)
        smobGroupDescription.postValue(null)
        smobGroupType.postValue(null)
    }

    // validate the entered data then saves the smobList to the DataSource
    @ExperimentalCoroutinesApi
    fun validateAndSaveSmobGroup(shopMobData: SmobGroupATO) {
        if (validateEnteredGroupData(shopMobData)) {
            saveSmobGroupItem(shopMobData)
            navigationCommand.value = NavigationCommand.Back
        }
    }

    // save the smobGroup item to the data source
    @ExperimentalCoroutinesApi
    private fun saveSmobGroupItem(smobGroupData: SmobGroupATO) {
        showLoading.value = true
        viewModelScope.launch {
            // store in local DB (and sync to server)
            groupDataSource.saveSmobItem(smobGroupData)
        }
        showLoading.value = false

        // load SmobLists from local DB to update StateFlow value
        collectAllSmobGroupsSF()

        // check if the "no data" symbol has to be shown (empty list)
        updateShowNoSmobItemsData(_smobGroupsSF.value)
    }

    // update the smobGroup item in the data source
    @ExperimentalCoroutinesApi
    fun updateSmobGroupItem(smobGroupData: SmobGroupATO) {
        showLoading.value = true
        viewModelScope.launch {
            // update in local DB (and sync to server)
            groupDataSource.updateSmobItem(smobGroupData)
        }
        showLoading.value = false

        // load SmobLists from local DB to update StateFlow value
        collectAllSmobGroupsSF()

        // check if the "no data" symbol has to be shown (empty list)
        updateShowNoSmobItemsData(_smobGroupsSF.value)
    }

    // validate the entered data and show error to the user if there's any invalid data
    private fun validateEnteredGroupData(shopMobData: SmobGroupATO): Boolean {

        if (shopMobData.name.isEmpty()) {
            showSnackBarInt.value = R.string.err_enter_group_name
            return false
        }

        // successful validation
        return true
    }


    // AdminGroupMemberListViewModel ---------------------------------------------
    // AdminGroupMemberListViewModel ---------------------------------------------
    // AdminGroupMemberListViewModel ---------------------------------------------

    // current group ID and group position (in the list of SmobGroups)
    var currGroupId: String? = null
    var currGroup: SmobGroupATO? = null

    // collect all SmobUsers
    private val smobUsersF: Flow<Resource<List<SmobUserATO>?>> = registerSmobUsersFlow()
    val smobUsersSF = smobUsersFlowAsSF(smobUsersF)

    // collect the upstream selected smobGroup as well as the list of SmobUserATO items
    // ... lateinit, as this can only be done once the fragment is created (and the id's are here)
    lateinit var smobGroupF: Flow<Resource<SmobGroupATO?>>
    lateinit var smobGroupSF: StateFlow<Resource<SmobGroupATO?>>

//    private val _smobGroupAltSF = MutableStateFlow<Resource<SmobGroupATO?>>(Resource.loading(null))
//    val smobGroupAltSF = _smobGroupAltSF.asStateFlow()  // read-only

    lateinit var smobGroupMembersF: Flow<Resource<List<SmobUserATO>?>>
    lateinit var smobGroupMembersSF: StateFlow<Resource<List<SmobUserATO>?>>

    lateinit var smobGroupMemberWithGroupDataSF: StateFlow<List<SmobGroupMemberWithGroupDataATO>?>


    // register the flow of the list of items for the upstream list the user just selected
    @ExperimentalCoroutinesApi
    fun registerSmobUsersFlow(): Flow<Resource<List<SmobUserATO>?>> =
        userDataSource.getAllSmobItems()

    // convert to StateFlow
    fun smobUsersFlowAsSF(inFlow: Flow<Resource<List<SmobUserATO>?>>):
            StateFlow<Resource<List<SmobUserATO>?>> =
        inFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.loading(null)
        )


    // register the flow of the upstream group the user just selected (flow still not collected)
    @ExperimentalCoroutinesApi
    fun registerSmobGroupFlow(id: String): Flow<Resource<SmobGroupATO?>> =
        groupDataSource.getSmobItem(id)

    // convert to StateFlow
    fun smobGroupFlowAsSF(inFlow: Flow<Resource<SmobGroupATO?>>):
            StateFlow<Resource<SmobGroupATO?>> =
        inFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.loading(null)
        )


    // register the flow of the list of items for the upstream list the user just selected
    @ExperimentalCoroutinesApi
    fun registerSmobGroupMembersFlow(id: String): Flow<Resource<List<SmobUserATO>?>> =
        userDataSource.getSmobMembersByGroupId(id)

    // convert to StateFlow
    fun smobGroupMembersFlowToSF(inFlow: Flow<Resource<List<SmobUserATO>?>>):
            StateFlow<Resource<List<SmobUserATO>?>> =
        inFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.loading(null)
        )


    // combine the two flows (users, groups [user status]) and turn into StateFlow
    @ExperimentalCoroutinesApi
    fun combineGroupAndUserFlowsSF(
        groupFlow: Flow<Resource<SmobGroupATO?>>,
        usersFlow: Flow<Resource<List<SmobUserATO>?>>,
    ): StateFlow<List<SmobGroupMemberWithGroupDataATO>?> {

        return usersFlow.combine(groupFlow) { users, group ->

            // unwrap group (from Resource)
            group.data?.let { daGroup ->

                // evaluate/unwrap Resource
                when(users.status) {

                    Status.SUCCESS -> {

                        // successfully received all users --> could be empty system though
                        users.data?.let { allUsers ->

                            // fetch all users as defined by the member list of the selected group
                            val daGroupUsers = allUsers.filter { user ->
                                daGroup.members.map { member -> member.id }.contains(user.id)
                            }

                            // return all users from daGroup member list
                            daGroupUsers.map { member ->

                                // extend user record by group data
                                SmobGroupMemberWithGroupDataATO(
                                    id = member.id,
                                    itemStatus = member.itemStatus,
                                    itemPosition = member.itemPosition,
                                    memberUsername = member.username,
                                    memberName = member.name,
                                    memberEmail = member.email,
                                    memberImageUrl = member.imageUrl,
                                    memberGroups = member.groups,
                                    groupId = daGroup.id,
                                    groupStatus = daGroup.itemStatus,
                                    groupPosition = daGroup.itemPosition,
                                    groupName = daGroup.name,
                                    groupDescription = daGroup.description,
                                    groupType = daGroup.type,
                                    groupMembers = daGroup.members,
                                    groupActivity = daGroup.activity,
                                )

                            }
                        }

                    }  // status == SUCCESS
                    else -> {
                        // Status.LOADING or Status.ERROR -> do nothing
                        null
                    }  // status != SUCCESS

                }  // when

            }  // unwrap group

        }  // combine(Flow_1, Flow_2)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = listOf()
            )  // StateFlow<...>

    }  //  combineGroupAndUserFlowsSF


//    // collect the flow of the upstream list the user just selected
//    @ExperimentalCoroutinesApi
//    fun collectSmobGroupAltSF() {
//
//        // group ID set yet?
//        currGroupId?.let { id ->
//
//            // collect flow
//            viewModelScope.launch {
//
//                // flow terminator
//                groupDataSource.getSmobItem(id)
//                    .catch { e ->
//                        // previously unhandled exception (= not handled at Room level)
//                        // --> catch it here and represent in Resource status
//                        _smobGroupAltSF.value = Resource.error(e.toString(), null)
//                        showSnackBar.value = _smobGroupAltSF.value.message
//                    }
//                    .collectLatest {
//                        // no exception during flow collection
//                        when(it.status) {
//                            Status.SUCCESS -> {
//                                // --> store successfully received data in StateFlow value
//                                _smobGroupAltSF.value = it
//                            }
//                            Status.ERROR -> {
//                                // these are errors handled at Room level --> display
//                                showSnackBar.value = it.message
//                                _smobGroupAltSF.value = it  // still return Resource value (w/h error)
//                            }
//                            Status.LOADING -> {
//                                // could control visibility of progress bar here
//                            }
//                        }
//                    }
//
//            }  // coroutine
//
//        } // groupId set
//
//    }  // collectSmobGroupAltSF


    //update all items in the local DB by querying the backend - triggered on "swipe down"
    @ExperimentalCoroutinesApi
    fun swipeRefreshUserDataInLocalDB() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {

            // update backend DB (from net API)
            userDataSource.refreshDataInLocalDB()

            // collect flow to update StateFlow with current value from DB
            smobGroupMembersSF.take(1).collect {

                if(it.status == Status.ERROR) {
                    showSnackBar.value = it.message!!
                }

                // check if the "no data" symbol has to be shown (empty list)
                updateShowNoSmobItemsData(it)
            }

        }

    }  // swipeRefreshUserDataInLocalDB


    // AdminGroupMemberDetailsViewModel ---------------------------------------------
    // AdminGroupMemberDetailsViewModel ---------------------------------------------
    // AdminGroupMemberDetailsViewModel ---------------------------------------------

    // current group member data record
    var currGroupMember: SmobUserATO? = null
    var currGroupMemberWithGroupData: SmobGroupMemberWithGroupDataATO? = null
    var enableAddButton: Boolean = false



    /*
     * =====================================================================================
     * SmobList handling
     * =====================================================================================
     */

    // AdminListsTable ---------------------------------------------------------------------
    // AdminListsTable ---------------------------------------------------------------------
    // AdminListsTable ---------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    // register flow for all SmobLists and provide as (un-collected) StateFlow
    private val smobListsF: Flow<Resource<List<SmobListATO>?>> = registerSmobListsFlow()
    val smobListsSF = smobListsFlowAsSF(smobListsF)  // SF

    // fetch flow of SmobLists and turn into Stateflow (SF) for direct collection in the UI
    @ExperimentalCoroutinesApi
    fun registerSmobListsFlow(): Flow<Resource<List<SmobListATO>?>> =
        listDataSource.getAllSmobItems()

    // convert flow to StateFlow (SF, not yet collected --> for direct collection in UI)
    fun smobListsFlowAsSF(inFlow: Flow<Resource<List<SmobListATO>?>>):
            StateFlow<Resource<List<SmobListATO>?>> =
        inFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.loading(null)
        )


    // -------------------------------------------------------------------------------------------
    // alternative formulation: directly define Stateflow (SF) of all SmobLists
    // ... ref: https://medium.com/androiddevelopers/migrating-from-livedata-to-kotlins-flow-379292f419fb
    private val _smobListsAltSF = MutableStateFlow<Resource<List<SmobListATO>>>(Resource.loading(null))
    val smobListsAltSF = _smobListsAltSF.asStateFlow()  // read-only

    // collect the flow of all SmobLists (--> lists table) into _smobListSF / smobListsSF
    @ExperimentalCoroutinesApi
    fun collectSmobListsFlowAsAltSF() {

        // collect flow
        listDataSource.getAllSmobItems()
            .catch { e ->
                // previously unhandled exception (= not handled at Room level)
                // --> catch it here and represent in Resource status
                _smobListsAltSF.value = Resource.error(e.toString(), null)
                showSnackBar.value = _smobListsAltSF.value.message
            }
            .take(1)
            .onEach {
                // no exception during flow collection
                when(it.status) {
                    Status.SUCCESS -> {
                        // --> store successfully received data in StateFlow value
                        _smobListsAltSF.value = it
                        updateShowNoSmobItemsData(it)
                    }
                    Status.ERROR -> {
                        // these are errors handled at Room level --> display
                        showSnackBar.value = it.message
                        _smobListsAltSF.value = it  // still return Resource value (w/h error)
                    }
                    Status.LOADING -> {
                        // could control visibility of progress bar here
                    }
                }
            }
            .launchIn(viewModelScope)  // co-routine scope

    }  // collectSmobListsFlowAsAltSF


    // update all items in the local DB by querying the backend - triggered on "swipe down"
    @ExperimentalCoroutinesApi
    fun swipeRefreshListDataInLocalDB() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {

            // update backend DB (from net API)
            listDataSource.refreshDataInLocalDB()

            // collect flow to update StateFlow with current value from DB
            smobListsSF.take(1).collect {

                if(it.status == Status.ERROR) {
                    showSnackBar.value = it.message!!
                }

                // check if the "no data" symbol has to be shown (empty list)
                updateShowNoSmobItemsData(it)
            }

        }

    }  // swipeRefreshListDataInLocalDB



    // AdminListGroupsTable -----------------------------------------------------------------
    // AdminListGroupsTable -----------------------------------------------------------------
    // AdminListGroupsTable -----------------------------------------------------------------

    // current list ID and snapshot of current list flow (collected in AdminListsTableFragment)
    var currListId: String? = null      // set/valid at creation of 'AdminListGroupsTableFragment'
    var currList: SmobListATO? = null   // set when the user clicks a list in the SmobList table


    // -------------------------------------------------------------------------------------------
    // flow and StateFlow of the upstream selected smobList
    // ... lateinit, as this can only be done once the fragment is created (currListId has been set)
    lateinit var smobListF: Flow<Resource<SmobListATO?>>
    lateinit var smobListSF: StateFlow<Resource<SmobListATO?>>

    // fetch the flow of the upstream SmobList the user just selected
    @ExperimentalCoroutinesApi
    fun registerSmobListFlow(id: String): Flow<Resource<SmobListATO?>> =
        listDataSource.getSmobItem(id)

    // convert flow to StateFlow (SF, not yet collected --> for direct collection in UI)
    fun registerSmobListFlowAsStateFlow(inFlow: Flow<Resource<SmobListATO?>>):
            StateFlow<Resource<SmobListATO?>> =
        inFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.loading(null)
        )


    // -------------------------------------------------------------------------------------------
    // alternative formulation: directly define Stateflow (SF) of currently selected SmobList
    // --> as per 'currListId' (above) that gets stored in currListId when the Fragment is created
    private val _currListAltSF = MutableStateFlow<Resource<SmobListATO?>>(Resource.loading(null))
    val currListAltSF = _currListAltSF.asStateFlow()  // read-only

    // collect the flow of the upstream list the user just selected and return it in StateFlow (SF)
    // variable '_currListAltSF/currListAltSF'
    @ExperimentalCoroutinesApi
    fun collectSmobListItemAsAltSF() {

        // list ID set yet?
        currListId?.let { id ->

            // collect flow
            viewModelScope.launch {

                // flow terminator
                listDataSource.getSmobItem(id)
                    .catch { e ->
                        // previously unhandled exception (= not handled at Room level)
                        // --> catch it here and represent in Resource status
                        _currListAltSF.value = Resource.error(e.toString(), null)
                        showSnackBar.value = _currListAltSF.value.message
                    }
                    .collectLatest {
                        // no exception during flow collection
                        when(it.status) {
                            Status.SUCCESS -> {
                                // --> store successfully received data in StateFlow value
                                _currListAltSF.value = it
                            }
                            Status.ERROR -> {
                                // these are errors handled at Room level --> display
                                showSnackBar.value = it.message
                                _currListAltSF.value = it  // still return Resource value (w/h error)
                            }
                            Status.LOADING -> {
                                // could control visibility of progress bar here
                            }
                        }
                    }

            }  // coroutine

        } // currListId set

    }  // collectSmobListItemAsAltSF



    // -------------------------------------------------------------------------------------------
    // flow and StateFlow of groups referenced in the upstream selected smobList
    // ... lateinit, as this can only be done once the fragment is created (currListId has been set)
    lateinit var smobListGroupsF: Flow<Resource<List<SmobGroupATO>?>>
    lateinit var smobListGroupsSF: StateFlow<Resource<List<SmobGroupATO>?>>

    // fetch the flow of the groups referred to by the upstream list the user just selected
    @ExperimentalCoroutinesApi
    fun registerSmobListGroupsFlow(id: String): Flow<Resource<List<SmobGroupATO>?>> =
        groupDataSource.getSmobGroupsByListId(id)

    // convert to StateFlow
    fun smobListGroupsFlowAsSF(inFlow: Flow<Resource<List<SmobGroupATO>?>>):
            StateFlow<Resource<List<SmobGroupATO>?>> =
        inFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.loading(null)
        )


    // -------------------------------------------------------------------------------------------
    // combined StateFlow of referenced groups list with corresponding list data
    lateinit var smobListGroupsWithListDataSF: StateFlow<List<SmobGroupWithListDataATO>?>

    // combine the two flows of a selected list (#1) and it's referenced groups (#2), return as SF
    @ExperimentalCoroutinesApi
    fun combineListGroupsAndListFlowSF(
        listFlow: Flow<Resource<SmobListATO?>>,
        groupFlow: Flow<Resource<List<SmobGroupATO>?>>,
    ): StateFlow<List<SmobGroupWithListDataATO>?> {

        return groupFlow.combine(listFlow) { groups, list ->

            // unwrap group (from Resource)
            list.data?.let { daList ->

                // evaluate/unwrap Resource
                when(groups.status) {

                    Status.SUCCESS -> {

                        // successfully received all groups --> could be empty system though
                        groups.data?.let { allGroups ->

                            // fetch all groups who refer to any of the groups associated with the
                            // selected list (daList.groups)
                            val daListGroups = allGroups.filter { group ->
                                daList.groups.map { listGroup -> listGroup.id }.contains(group.id)
                            }

                            // return all groups associated with daList, incl. the list details
                            daListGroups.map { group ->

                                // extend user record by group data
                                SmobGroupWithListDataATO(
                                    id = group.id,
                                    itemStatus = group.itemStatus,
                                    itemPosition = group.itemPosition,
                                    groupName = group.name,
                                    groupDescription = group.description,
                                    groupType = group.type,
                                    groupMembers = group.members,
                                    groupActivity = group.activity,
                                    listId = daList.id,
                                    listStatus = daList.itemStatus,
                                    listPosition = daList.itemPosition,
                                    listName = daList.name,
                                    listDescription = daList.description,
                                    listItems = daList.items,
                                    listGroups = daList.groups,
                                    listLifecycle = daList.lifecycle,
                                )

                            }
                        }

                    }  // status == SUCCESS
                    else -> {
                        // Status.LOADING or Status.ERROR -> do nothing
                        null
                    }  // status != SUCCESS

                }  // when

            }  // unwrap group

        }  // combine(Flow_1, Flow_2)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = listOf()
            )  // StateFlow<...>

    }  //  combineListGroupsAndListFlowSF



    // AdminListsAddNewItem --------------------------------------------------------------
    // AdminListsAddNewItem --------------------------------------------------------------
    // AdminListsAddNewItem --------------------------------------------------------------

    val smobListName = MutableLiveData<String?>()
    val smobListDescription = MutableLiveData<String?>()
    val smobListStatus = MutableLiveData<SmobItemStatus?>()

    init {
        onClearList()
    }

    /**
     * Clear the live data objects to start fresh next time the view model gets instantiated
     */
    fun onClearList() {
        // viewModel is initiated in background "doWork" job (coroutine)
        smobListName.postValue(null)
        smobListDescription.postValue(null)
        smobListStatus.postValue(null)
    }

    /**
     * Validate the entered data then saves the smobList to the DataSource
     */
    @ExperimentalCoroutinesApi
    fun validateAndSaveSmobList(shopMobData: SmobListATO) {
        if (validateEnteredListData(shopMobData)) {
            saveSmobListItem(shopMobData)
            navigationCommand.value = NavigationCommand.Back
        }
    }

    /**
     * Save the smobGroup item to the data source
     */
    @ExperimentalCoroutinesApi
    private fun saveSmobListItem(smobListData: SmobListATO) {
        showLoading.value = true
        viewModelScope.launch {
            // store in local DB (and sync to server)
            listDataSource.saveSmobItem(smobListData)
        }
        showLoading.value = false

        // load SmobList from local DB to update StateFlow value
        collectSmobListsFlowAsAltSF()

        // check if the "no data" symbol has to be shown (empty list)
        updateShowNoSmobItemsData(smobListsAltSF.value)
    }

    /**
     * Update the smobGroup item in the data source
     */
    @ExperimentalCoroutinesApi
    fun updateSmobListItem(smobListData: SmobListATO) {
        showLoading.value = true
        viewModelScope.launch {
            // update in local DB (and sync to server)
            listDataSource.updateSmobItem(smobListData)
        }
        showLoading.value = false

        // load SmobLists from local DB to update StateFlow value
        collectSmobListsFlowAsAltSF()

        // check if the "no data" symbol has to be shown (empty list)
        updateShowNoSmobItemsData(_smobListsAltSF.value)
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
    private fun validateEnteredListData(shopMobData: SmobListATO): Boolean {

        if (shopMobData.name.isEmpty()) {
            showSnackBarInt.value = R.string.err_enter_list_name
            return false
        }

        // successful validation
        return true
    }


    // AdminListGroupDetails --------------------------------------------------------------
    // AdminListGroupDetails --------------------------------------------------------------
    // AdminListGroupDetails --------------------------------------------------------------

    // current list group data record
    var currGroupDetail: SmobGroupATO? = null
    var currGroupWithListData: SmobGroupWithListDataATO? = null



    // AdminListGroupSelect ---------------------------------------------------------------
    // AdminListGroupSelect ---------------------------------------------------------------
    // AdminListGroupSelect ---------------------------------------------------------------

    // -------------------------------------------------------------------------------------------
    // flow and StateFlow of groups referenced in the upstream selected smobList
    // ... lateinit, as this can only be done once the fragment is created (currListId has been set)
    lateinit var allSmobGroupsF: Flow<Resource<List<SmobGroupATO>?>>
    lateinit var allSmobGroupsSF: StateFlow<Resource<List<SmobGroupATO>?>>

    // fetch the flow of the groups referred to by the upstream list the user just selected
    @ExperimentalCoroutinesApi
    fun registerAllSmobGroupsFlow(): Flow<Resource<List<SmobGroupATO>?>> =
        groupDataSource.getAllSmobItems()

    // convert to StateFlow
    fun allSmobGroupsFlowAsSF(inFlow: Flow<Resource<List<SmobGroupATO>?>>):
            StateFlow<Resource<List<SmobGroupATO>?>> =
        inFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.loading(null)
        )

    // -------------------------------------------------------------------------------------------
    // combined StateFlow of all groups with selected list data
    lateinit var smobAllGroupsWithListDataSF: StateFlow<List<SmobGroupWithListDataATO>?>

    // combine the two flows (selected list, referenced groups) and return as StateFlow
    @ExperimentalCoroutinesApi
    fun combineAllGroupsAndListFlowSF(
        listFlow: Flow<Resource<SmobListATO?>>,
        groupsFlow: Flow<Resource<List<SmobGroupATO>?>>,
    ): StateFlow<List<SmobGroupWithListDataATO>?> {

        return groupsFlow.combine(listFlow) { groups, list ->

            // unwrap group (from Resource)
            list.data?.let { daList ->

                // evaluate/unwrap Resource
                when(groups.status) {

                    Status.SUCCESS -> {

                        // successfully received all groups --> could be empty system though
                        groups.data?.let { allGroups ->

                            // fetch all groups who do not (yet) refer to any of the groups
                            // associated with the selected list (daList.groups)... filterNOT
                            val daListGroups = allGroups.filterNot { group ->
                                daList.groups.map { listGroup -> listGroup.id }.contains(group.id)
                            }

                            // return all groups associated with daList, incl. the list details
                            daListGroups.map { group ->

                                // extend user record by group data
                                SmobGroupWithListDataATO(
                                    id = group.id,
                                    itemStatus = group.itemStatus,
                                    itemPosition = group.itemPosition,
                                    groupName = group.name,
                                    groupDescription = group.description,
                                    groupType = group.type,
                                    groupMembers = group.members,
                                    groupActivity = group.activity,
                                    listId = daList.id,
                                    listStatus = daList.itemStatus,
                                    listPosition = daList.itemPosition,
                                    listName = daList.name,
                                    listDescription = daList.description,
                                    listItems = daList.items,
                                    listGroups = daList.groups,
                                    listLifecycle = daList.lifecycle,
                                )

                            }
                        }

                    }  // status == SUCCESS
                    else -> {
                        // Status.LOADING or Status.ERROR -> do nothing
                        null
                    }  // status != SUCCESS

                }  // when

            }  // unwrap group

        }  // combine(Flow_1, Flow_2)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = listOf()
            )  // StateFlow<...>

    }  //  combineAllGroupsAndListFlowSF

}