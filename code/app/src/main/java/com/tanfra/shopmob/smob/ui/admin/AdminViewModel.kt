package com.tanfra.shopmob.smob.ui.admin

import android.app.Application
import android.database.Cursor
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.types.GroupType
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.repo.ato.*
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseViewModel
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobGroupRepository
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobListRepository
import com.tanfra.shopmob.smob.data.repo.repoIf.SmobUserRepository
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.ato.SmobContactATO
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationCommand
import com.tanfra.shopmob.smob.ui.zeUtils.combineFlows
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class AdminViewModel(
    private val app: Application,
    val groupRepository: SmobGroupRepository,  // public - used in AdminGroupsAdapter
    val listRepository: SmobListRepository,    // public - used in AdminListsAdapter
    private val userRepository: SmobUserRepository,    // public - used in AdminGroupMemberAdapter
    ) : BaseViewModel(app) {


    // Import contact details
    // Import contact details
    // Import contact details

    // adapted from: https://medium.com/@kednaik/android-contacts-fetching-using-coroutines-aa0129bffdc4

    private val contactsMLD = MutableLiveData<List<SmobContactATO>>()
    val contactsLD: LiveData<List<SmobContactATO>> = contactsMLD

    // currently selected contact (by clicking one in the list)
    var currSmobContactATO: SmobContactATO? = null

    fun fetchContacts() {
        viewModelScope.launch {
            val contactsListAsync = async { getDeviceContacts() }
            val contactNumbersAsync = async { getContactNumbers() }
            val contactEmailAsync = async { getContactEmails() }

            val contacts = contactsListAsync.await()
            val contactNumbers = contactNumbersAsync.await()
            val contactEmails = contactEmailAsync.await()

            // add numbers & emails to contacts
            contacts.forEach { contact ->
                contactNumbers[contact.id]?.let { numbers ->
                    numbers.map { number -> contact.numbers.add(number) }
                }
                contactEmails[contact.id]?.let { emails ->
                    emails.map { email -> contact.emails.add(email) }
                }
            }
            contactsMLD.postValue(contacts)
        }
    }

    private fun getDeviceContacts(): List<SmobContactATO> {
        val contactsList = mutableListOf<SmobContactATO>()
        val contactsCursor = app.contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")
        if (contactsCursor != null && contactsCursor.count > 0) {
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            var idx: Long = 0
            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getString(idIndex)
                val name = contactsCursor.getString(nameIndex)
                if (name != null) {
                    contactsList.add(SmobContactATO(
                        id,
                        ItemStatus.NEW,
                        idx,
                        name
                    ))
                    idx += 1
                }
            }
            contactsCursor.close()
        }
        return contactsList
    }

    private fun getContactNumbers(): HashMap<String, MutableList<String>> {
        val contactsNumberMap = HashMap<String, MutableList<String>>()
        val phoneCursor: Cursor? = app.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (phoneCursor != null && phoneCursor.count > 0) {
            val contactIdIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val numberIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (phoneCursor.moveToNext()) {
                val contactId = phoneCursor.getString(contactIdIndex)
                val number: String = phoneCursor.getString(numberIndex)
                //check if the map contains key or not, if not then create a new array list with number
                if (contactsNumberMap.containsKey(contactId)) {
                    contactsNumberMap[contactId]?.add(number)
                } else {
                    contactsNumberMap[contactId] = mutableListOf(number)
                }
            }
            //contact contains all the number of a particular contact
            phoneCursor.close()
        }
        return contactsNumberMap
    }

    private fun getContactEmails(): HashMap<String, MutableList<String>> {
        val contactsEmailMap = HashMap<String, MutableList<String>>()
        val emailCursor = app.contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            null,
            null,
            null,
            null)
        if (emailCursor != null && emailCursor.count > 0) {
            val contactIdIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
            val emailIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
            while (emailCursor.moveToNext()) {
                val contactId = emailCursor.getString(contactIdIndex)
                val email = emailCursor.getString(emailIndex)
                //check if the map contains key or not, if not then create a new array list with email
                if (contactsEmailMap.containsKey(contactId)) {
                    contactsEmailMap[contactId]?.add(email)
                } else {
                    contactsEmailMap[contactId] = mutableListOf(email)
                }
            }
            //contact contains all the emails of a particular contact
            emailCursor.close()
        }
        return contactsEmailMap
    }


    // AdminGroupsViewModel --------------------------------------------
    // AdminGroupsViewModel --------------------------------------------
    // AdminGroupsViewModel --------------------------------------------


    // list that holds the smob data items to be displayed on the UI
    // static StateFlows (independent of user choice / id)
    val smobGroupsSF = groupRepository.getSmobItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Empty
        )


    /**
     * collect the flow of the list of SmobShops
     */
    @ExperimentalCoroutinesApi
    private fun collectSmobGroups() {

        viewModelScope.launch {

            // collect flow / update SF
            smobGroupsSF
                .take(1)
                .collect {
                    when (it) {
                        is Resource.Empty -> Timber.i("SmobGroups still loading")
                        is Resource.Failure -> {
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

    }  // collectSmobGroups


    /**
     * update all items in the local DB by querying the backend - triggered on "swipe down"
     */
    @ExperimentalCoroutinesApi
    fun swipeRefreshGroupDataInLocalDB() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {

            // update backend DB (from net API)
            groupRepository.refreshItemsInLocalDB()

            // collect flow and update StateFlow values (to get it out of the initial loading state)
            collectSmobGroups()

            // check if the "no data" symbol has to be shown (empty list)
            updateShowNoData(smobGroupsSF.value)

        }

    }  // swipeRefreshShopDataInLocalDB


    // inform the user that the list of SmobItems is empty
    @ExperimentalCoroutinesApi
    private fun updateShowNoData(smobListNewest: Resource<List<*>>) {
        showNoData.value = when(smobListNewest) {
            is Resource.Failure -> false
            is Resource.Empty -> true
            is Resource.Success -> {
                smobListNewest.data.isEmpty() ||
                smobListNewest.data.all { (it as Ato).status == ItemStatus.DELETED }
            }
        }
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
            groupRepository.saveSmobItem(smobGroupData)
        }
        showLoading.value = false

        // collect flow and update StateFlow values (to get it out of the initial loading state)
        collectSmobGroups()

        // check if the "no data" symbol has to be shown (empty list)
        updateShowNoData(smobGroupsSF.value)

    }

    // update the smobGroup item in the data source
    @ExperimentalCoroutinesApi
    fun updateSmobGroupItem(smobGroupData: SmobGroupATO) {
        showLoading.value = true
        viewModelScope.launch {
            // update in local DB (and sync to server)
            groupRepository.updateSmobItem(smobGroupData)
        }
        showLoading.value = false

        // collect flow and update StateFlow values (to get it out of the initial loading state)
        collectSmobGroups()

        // check if the "no data" symbol has to be shown (empty list)
        updateShowNoData(smobGroupsSF.value)

    }

    // update the smobUser item in the data source
    @ExperimentalCoroutinesApi
    fun updateSmobUserItem(smobUserData: SmobUserATO) {
        showLoading.value = true
        viewModelScope.launch {
            // update in local DB (and sync to server)
            userRepository.updateSmobItem(smobUserData)
        }
        showLoading.value = false
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


    // access to the upstream selected smobGroup as well as the list of SmobUserATO items
    // ... lateinit, as this can only be done once the fragment is created (and the id's are here)
    lateinit var smobGroupF: Flow<Resource<SmobGroupATO>>
    lateinit var smobGroupMembersF: Flow<Resource<List<SmobUserATO>>>

    lateinit var smobGroupSF: StateFlow<Resource<SmobGroupATO>>
    lateinit var smobGroupMembersSF: StateFlow<Resource<List<SmobUserATO>>>
    lateinit var smobGroupMemberWithGroupDataSF: StateFlow<List<SmobGroupMemberWithGroupDataATO>>


    // access to (static) flows
    val smobUsersSF = userRepository.getSmobItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Empty
        )


    // register the flow of the upstream group the user just selected (flow still not collected)
    @ExperimentalCoroutinesApi
    fun registerSmobGroupFlow(id: String): Flow<Resource<SmobGroupATO>> =
        groupRepository.getSmobItem(id)

    // convert to StateFlow
    fun smobGroupFlowAsSF(inFlow: Flow<Resource<SmobGroupATO>>):
            StateFlow<Resource<SmobGroupATO>> =
        inFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Empty
        )


    // register the flow of the list of items for the upstream list the user just selected
    @ExperimentalCoroutinesApi
    fun registerSmobGroupMembersFlow(id: String): Flow<Resource<List<SmobUserATO>>> =
        userRepository.getSmobMembersByGroupId(id)

    // convert to StateFlow
    fun smobGroupMembersFlowToSF(inFlow: Flow<Resource<List<SmobUserATO>>>):
            StateFlow<Resource<List<SmobUserATO>>> =
        inFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Empty
        )


    /**
     * combine the two flows (users, groups [user status]) and turn into StateFlow
     */
    @ExperimentalCoroutinesApi
    fun combineGroupAndUserFlowsSF(
        groupResFlow: Flow<Resource<SmobGroupATO>>,
        userListResFlow: Flow<Resource<List<SmobUserATO>>>,
    ): StateFlow<List<SmobGroupMemberWithGroupDataATO>> =
        combineFlows(groupResFlow, userListResFlow) { daGroup, daUsers ->

            // fetch all users as defined by the member list of the selected group
            val daGroupUsers = daUsers.filter { user ->
                daGroup.members
                    .map { member -> member.id }
                    .contains(user.id)
            }

            // return all (other) users, except those from daGroup member list
            daGroupUsers.map { member ->

                // extend user record by group data
                SmobGroupMemberWithGroupDataATO(
                    id = member.id,
                    status = member.status,
                    position = member.position,
                    memberUsername = member.username,
                    memberName = member.name,
                    memberEmail = member.email,
                    memberImageUrl = member.imageUrl,
                    memberGroups = member.groups,
                    groupId = daGroup.id,
                    groupStatus = daGroup.status,
                    groupPosition = daGroup.position,
                    groupName = daGroup.name,
                    groupDescription = daGroup.description,
                    groupType = daGroup.type,
                    groupMembers = daGroup.members,
                    groupActivity = daGroup.activity,
                )
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = listOf()
            )


    //update all items in the local DB by querying the backend - triggered on "swipe down"
    @ExperimentalCoroutinesApi
    fun swipeRefreshUserDataInLocalDB() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {

            // update backend DB (from net API)
            userRepository.refreshItemsInLocalDB()

            // collect flow to update StateFlow with current value from DB
            smobGroupMembersSF.take(1).collect {

                when (it) {
                    is Resource.Failure -> { showSnackBar.value = it.exception.message }
                    is Resource.Empty -> Timber.i("SmobUser still loading")
                    is Resource.Success -> updateShowNoData(it)
                }

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
    var backDestinationId: Int? = null



    /*
     * =====================================================================================
     * SmobList handling
     * =====================================================================================
     */

    // AdminListsTable ---------------------------------------------------------------------
    // AdminListsTable ---------------------------------------------------------------------
    // AdminListsTable ---------------------------------------------------------------------

    // StateFlow for all SmobLists
    val smobListsSF = listRepository.getSmobItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Empty
        )


    /**
     * collect the flow of the list of SmobShops
     */
    @ExperimentalCoroutinesApi
    private fun collectSmobLists() {

        viewModelScope.launch {

            // collect flow / update SF
            smobListsSF
                .take(1)
                .collect {
                    when (it) {
                        is Resource.Empty -> Timber.i("SmobLists still loading")
                        is Resource.Failure -> {
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

    }  // collectSmobLists


    // update all items in the local DB by querying the backend - triggered on "swipe down"
    @ExperimentalCoroutinesApi
    fun swipeRefreshListDataInLocalDB() {

        // user is impatient - trigger update of local DB from net
        viewModelScope.launch {

            // update backend DB (from net API)
            listRepository.refreshItemsInLocalDB()

            // collect flow to update StateFlow with current value from DB
            smobListsSF.take(1).collect {

                when (it) {
                    is Resource.Failure -> { showSnackBar.value = it.exception.message }
                    is Resource.Empty -> Timber.i("SmobList still loading")
                    is Resource.Success -> updateShowNoData(it)
                }

            }

        }

    }  // swipeRefreshListDataInLocalDB



    // AdminListGroupsTable -----------------------------------------------------------------
    // AdminListGroupsTable -----------------------------------------------------------------
    // AdminListGroupsTable -----------------------------------------------------------------

    // snapshot of current list flow (collected in AdminListsTableFragment)
    var currList: SmobListATO? = null   // set when the user clicks a list in the SmobList table


    // -------------------------------------------------------------------------------------------
    // flow and StateFlow of the upstream selected smobList
    // ... lateinit, as this can only be done once the fragment is created (currListId has been set)
    lateinit var smobListF: Flow<Resource<SmobListATO>>
    lateinit var smobListSF: StateFlow<Resource<SmobListATO>>

    // fetch the flow of the upstream SmobList the user just selected
    @ExperimentalCoroutinesApi
    fun registerSmobListFlow(id: String): Flow<Resource<SmobListATO>> =
        listRepository.getSmobItem(id)

    // convert flow to StateFlow (SF, not yet collected --> for direct collection in UI)
    fun registerSmobListFlowAsStateFlow(inFlow: Flow<Resource<SmobListATO>>):
            StateFlow<Resource<SmobListATO>> =
        inFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Empty
        )


    // -------------------------------------------------------------------------------------------
    // flow and StateFlow of groups referenced in the upstream selected smobList
    // ... lateinit, as this can only be done once the fragment is created (currListId has been set)
    lateinit var smobListGroupsF: Flow<Resource<List<SmobGroupATO>>>
    lateinit var smobListGroupsSF: StateFlow<Resource<List<SmobGroupATO>>>

    // fetch the flow of the groups referred to by the upstream list the user just selected
    @ExperimentalCoroutinesApi
    fun registerSmobListGroupsFlow(id: String): Flow<Resource<List<SmobGroupATO>>> =
        groupRepository.getSmobGroupsByListId(id)

    // convert to StateFlow
    fun smobListGroupsFlowAsSF(inFlow: Flow<Resource<List<SmobGroupATO>>>):
            StateFlow<Resource<List<SmobGroupATO>>> =
        inFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Empty
        )


    // -------------------------------------------------------------------------------------------
    // combined StateFlow of referenced groups list with corresponding list data
    lateinit var smobListGroupsWithListDataSF: StateFlow<List<SmobGroupWithListDataATO>>


    /**
     * combine the two flows of a selected list (#1) and it's referenced groups (#2), return as SF
     */
    @ExperimentalCoroutinesApi
    fun combineListGroupsAndListFlowSF(
        listResFlow: Flow<Resource<SmobListATO>>,
        groupListResFlow: Flow<Resource<List<SmobGroupATO>>>,
    ): StateFlow<List<SmobGroupWithListDataATO>> =
        combineFlows(listResFlow, groupListResFlow) { daList, daGroups ->

            // fetch all groups who refer to any of the groups associated with the
            // selected list (daList.groups)
            val daListGroups = daGroups.filter { group ->
                daList.groups.map { listGroup -> listGroup.id }
                    .contains(group.id)
            }

            // return all groups associated with daList, incl. the list details
            daListGroups.map { group ->

                // extend user record by group data
                SmobGroupWithListDataATO(
                    id = group.id,
                    status = group.status,
                    position = group.position,
                    groupName = group.name,
                    groupDescription = group.description,
                    groupType = group.type,
                    groupMembers = group.members,
                    groupActivity = group.activity,
                    listId = daList.id,
                    listStatus = daList.status,
                    listPosition = daList.position,
                    listName = daList.name,
                    listDescription = daList.description,
                    listItems = daList.items,
                    listGroups = daList.groups,
                    listLifecycle = daList.lifecycle,
                )
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = listOf()
            )


    // AdminListsAddNewItem --------------------------------------------------------------
    // AdminListsAddNewItem --------------------------------------------------------------
    // AdminListsAddNewItem --------------------------------------------------------------

    val smobListName = MutableLiveData<String?>()
    val smobListDescription = MutableLiveData<String?>()
    private val smobListStatus = MutableLiveData<ItemStatus?>()

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
     * Update the smobGroup item in the data source
     */
    @ExperimentalCoroutinesApi
    fun updateSmobListItem(smobListData: SmobListATO) {
        showLoading.value = true
        viewModelScope.launch {
            // update in local DB (and sync to server)
            listRepository.updateSmobItem(smobListData)
        }
        showLoading.value = false

        // load SmobLists from local DB to update StateFlow value
        this.collectSmobLists()

        // check if the "no data" symbol has to be shown (empty list)
        updateShowNoData(smobListsSF.value)
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
    lateinit var allSmobGroupsF: Flow<Resource<List<SmobGroupATO>>>

    // fetch the flow of the groups referred to by the upstream list the user just selected
    @ExperimentalCoroutinesApi
    fun registerAllSmobGroupsFlow(): Flow<Resource<List<SmobGroupATO>>> =
        groupRepository.getSmobItems()

    // -------------------------------------------------------------------------------------------
    // combined StateFlow of all groups with selected list data
    lateinit var smobAllGroupsWithListDataSF: StateFlow<List<SmobGroupWithListDataATO>>

    /**
     * combine the two flows of a selected list (#1) and it's referenced groups (#2), return as SF
     */
    @ExperimentalCoroutinesApi
    fun combineAllGroupsAndListFlowSF(
        listResFlow: Flow<Resource<SmobListATO>>,
        groupListResFlow: Flow<Resource<List<SmobGroupATO>>>,
    ): StateFlow<List<SmobGroupWithListDataATO>> =
        combineFlows(listResFlow, groupListResFlow) { daList, daGroups ->

            // fetch all groups who refer to any of the groups associated with the
            // selected list (daList.groups)
            val daListGroups = daGroups
                .filter { group ->
                    daList.groups
                        .map { listGroup ->
                            if (listGroup.status != ItemStatus.DELETED)
                                listGroup.id
                            else
                                ""
                        }
                        .contains(group.id)
                }

            // fetch all groups which are not yet referred to by the selected list
            val daOtherGroups = daGroups.subtract(daListGroups.toSet())

            // return all groups associated with daList, incl. the list details
            daOtherGroups.map { group ->

                // extend user record by group data
                SmobGroupWithListDataATO(
                    id = group.id,
                    status = group.status,
                    position = group.position,
                    groupName = group.name,
                    groupDescription = group.description,
                    groupType = group.type,
                    groupMembers = group.members,
                    groupActivity = group.activity,
                    listId = daList.id,
                    listStatus = daList.status,
                    listPosition = daList.position,
                    listName = daList.name,
                    listDescription = daList.description,
                    listItems = daList.items,
                    listGroups = daList.groups,
                    listLifecycle = daList.lifecycle,
                )

            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = listOf()
            )

}