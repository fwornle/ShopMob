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
import com.tanfra.shopmob.smob.data.repo.utils.Member
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class AdminViewModel(
    app: Application,
    val groupDataSource: SmobGroupDataSource,  // public - used in AdminGroupsAdapter
    val listDataSource: SmobListDataSource,  // public - used in AdminListsAdapter
    val userDataSource: SmobUserDataSource,    // public - used in AdminGroupMembersAdapter
    ) : BaseViewModel(app) {

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
    fun fetchSmobGroups() {

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

    }  // fetchSmobGroups


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
            fetchSmobGroups()

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


    // GroupsListEditViewModel ---------------------------------------------
    // GroupsListEditViewModel ---------------------------------------------
    // GroupsListEditViewModel ---------------------------------------------

    val smobGroupName = MutableLiveData<String?>()
    val smobGroupDescription = MutableLiveData<String?>()
    val smobGroupType = MutableLiveData<GroupType?>()

    init {
        onClearGroup()
    }

    /**
     * Clear the live data objects to start fresh next time the view model gets instantiated
     */
    fun onClearGroup() {
        // viewModel is initiated in background "doWork" job (coroutine)
        smobGroupName.postValue(null)
        smobGroupDescription.postValue(null)
        smobGroupType.postValue(null)
    }

    /**
     * Validate the entered data then saves the smobList to the DataSource
     */
    @ExperimentalCoroutinesApi
    fun validateAndSaveSmobGroup(shopMobData: SmobGroupATO) {
        if (validateEnteredData(shopMobData)) {
            saveSmobGroupItem(shopMobData)
            navigationCommand.value = NavigationCommand.Back
        }
    }

    /**
     * Save the smobGroup item to the data source
     */
    @ExperimentalCoroutinesApi
    private fun saveSmobGroupItem(smobGroupData: SmobGroupATO) {
        showLoading.value = true
        viewModelScope.launch {
            // store in local DB (and sync to server)
            groupDataSource.saveSmobItem(smobGroupData)
        }
        showLoading.value = false

        // load SmobLists from local DB to update StateFlow value
        fetchSmobGroups()

        // check if the "no data" symbol has to be shown (empty list)
        updateShowNoData(_smobGroups.value)
    }

    /**
     * Update the smobGroup item in the data source
     */
    @ExperimentalCoroutinesApi
    fun updateSmobGroupItem(smobGroupData: SmobGroupATO) {
        showLoading.value = true
        viewModelScope.launch {
            // update in local DB (and sync to server)
            groupDataSource.updateSmobItem(smobGroupData)
        }
        showLoading.value = false

        // load SmobLists from local DB to update StateFlow value
        fetchSmobGroups()

        // check if the "no data" symbol has to be shown (empty list)
        updateShowNoData(_smobGroups.value)
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
    private fun validateEnteredData(shopMobData: SmobGroupATO): Boolean {

        if (shopMobData.name.isEmpty()) {
            showSnackBarInt.value = R.string.err_enter_group_name
            return false
        }

        // successful validation
        return true
    }


    // GroupMembersListViewModel ---------------------------------------------
    // GroupMembersListViewModel ---------------------------------------------
    // GroupMembersListViewModel ---------------------------------------------

    // current group ID and group position (in the list of SmobGroups)
    var currGroupId: String? = null
    var currGroup: SmobGroupATO? = null

    // collect all SmobUsers
    val _smobUsers: Flow<Resource<List<SmobUserATO>?>> = fetchSmobUsersFlow()
    val smobUsers = smobUsersFlowToStateFlow(_smobUsers)

    // collect the upstream selected smobGroup as well as the list of SmobUserATO items
    // ... lateinit, as this can only be done once the fragment is created (and the id's are here)
    lateinit var _smobGroup: Flow<Resource<SmobGroupATO?>>
    lateinit var smobGroup: StateFlow<Resource<SmobGroupATO?>>

    val _smobGroup2 = MutableStateFlow<Resource<SmobGroupATO?>>(Resource.loading(null))
    val smobGroup2 = _smobGroup2.asStateFlow()

    lateinit var _smobGroupMembers: Flow<Resource<List<SmobUserATO>?>>
    lateinit var smobGroupMembers: StateFlow<Resource<List<SmobUserATO>?>>

    lateinit var smobGroupMemberWithGroupData: StateFlow<List<SmobGroupMemberWithGroupDataATO>?>


    /**
     * fetch the flow of the list of items for the upstream list the user just selected
     */
    @ExperimentalCoroutinesApi
    fun fetchSmobUsersFlow(): Flow<Resource<List<SmobUserATO>?>> {
        val fetchFlow = userDataSource.getAllSmobItems()
        return fetchFlow
    }

    // convert to StateFlow
    fun smobUsersFlowToStateFlow(inFlow: Flow<Resource<List<SmobUserATO>?>>): StateFlow<Resource<List<SmobUserATO>?>> {
        return inFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.loading(null)
        )  // StateFlow<...>
    }


    /**
     * fetch the flow of the upstream group the user just selected
     */
    @ExperimentalCoroutinesApi
    fun fetchSmobGroupFlow(id: String): Flow<Resource<SmobGroupATO?>> {
        val fetchFlow = groupDataSource.getSmobItem(id)
        return fetchFlow
    }

    // convert to StateFlow
    fun smobGroupFlowToStateFlow(inFlow: Flow<Resource<SmobGroupATO?>>): StateFlow<Resource<SmobGroupATO?>> {
        return inFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.loading(null)
        )  // StateFlow<...>
    }

    /**
     * fetch the flow of the list of items for the upstream list the user just selected
     */
    @ExperimentalCoroutinesApi
    fun fetchSmobGroupMembersFlow(id: String): Flow<Resource<List<SmobUserATO>?>> {
        val fetchFlow = userDataSource.getSmobMembersByGroupId(id)
        return fetchFlow
    }

    // convert to StateFlow
    fun smobGroupMembersFlowToStateFlow(inFlow: Flow<Resource<List<SmobUserATO>?>>): StateFlow<Resource<List<SmobUserATO>?>> {
        return inFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.loading(null)
        )  // StateFlow<...>
    }

    /**
     * combine the two flows (users, groups [user status]) and turn into StateFlow
     */
    @ExperimentalCoroutinesApi
    fun combineFlowsAndConvertToStateFlow(
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

    }  //  combineFlowsAndConvertToStateFlow


    /**
     * collect the flow of the upstream list the user just selected
     */
    @ExperimentalCoroutinesApi
    fun fetchSmobGroup() {

        // group ID set yet?
        currGroupId?.let { id ->

            // collect flow
            viewModelScope.launch {

                // flow terminator
                groupDataSource.getSmobItem(id)
                    .catch { e ->
                        // previously unhandled exception (= not handled at Room level)
                        // --> catch it here and represent in Resource status
                        _smobGroup2.value = Resource.error(e.toString(), null)
                        showSnackBar.value = _smobGroup2.value.message
                    }
                    .collectLatest {
                        // no exception during flow collection
                        when(it.status) {
                            Status.SUCCESS -> {
                                // --> store successfully received data in StateFlow value
                                _smobGroup2.value = it
                            }
                            Status.ERROR -> {
                                // these are errors handled at Room level --> display
                                showSnackBar.value = it.message
                                _smobGroup2.value = it  // still return Resource value (w/h error)
                            }
                            Status.LOADING -> {
                                // could control visibility of progress bar here
                            }
                        }
                    }

            }  // coroutine

        } // groupId set

    }  // fetchSmobGroup

    /**
     * update all items in the local DB by querying the backend - triggered on "swipe down"
     */
    @ExperimentalCoroutinesApi
    fun swipeRefreshUserDataInLocalDB() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {

            // update backend DB (from net API)
            userDataSource.refreshDataInLocalDB()

            // collect flow to update StateFlow with current value from DB
            smobGroupMembers.take(1).collect {

                if(it.status == Status.ERROR) {
                    showSnackBar.value = it.message!!
                }

                // check if the "no data" symbol has to be shown (empty list)
                updateShowNoData(it)
            }

        }

    }  // swipeRefreshUserDataInLocalDB


    // GroupMemberDetailsViewModel ---------------------------------------------
    // GroupMemberDetailsViewModel ---------------------------------------------
    // GroupMemberDetailsViewModel ---------------------------------------------

    // current group member data record
    var currGroupMember: SmobUserATO? = null
    var currGroupMemberWithGroupData: SmobGroupMemberWithGroupDataATO? = null
    var currMemberDetails: Member? = null
    var enableAddButton: Boolean = false
}