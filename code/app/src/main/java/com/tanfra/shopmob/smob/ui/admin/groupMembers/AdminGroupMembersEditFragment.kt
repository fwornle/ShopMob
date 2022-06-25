package com.tanfra.shopmob.smob.ui.admin.groupMembers

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import com.tanfra.shopmob.R
import com.tanfra.shopmob.SmobApp
import com.tanfra.shopmob.databinding.FragmentAdminGroupMembersEditBinding
import com.tanfra.shopmob.smob.data.local.utils.*
import com.tanfra.shopmob.smob.data.repo.ato.SmobGroupATO
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import com.tanfra.shopmob.smob.ui.planning.utils.closeSoftKeyboard
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinComponent
import java.util.*


class AdminGroupMembersEditFragment : BaseFragment(), KoinComponent {

    // get the view model (from Koin service locator) ... shared with PlanningListsFragment
    override val _viewModel: AdminViewModel by sharedViewModel()

    // data binding of underlying layout
    private lateinit var binding: FragmentAdminGroupMembersEditBinding

    // assemble smobList data item
    private lateinit var daSmobGroupATO: SmobGroupATO

    // new lists are created at the highest position (+1)
    private var listPosMax: Long = 0L


    // create fragment view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // inflate fragment layout and return binding object
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_admin_group_members_edit, container, false)

        // fetch currently highest list position from incoming bundle
        listPosMax = arguments?.getLong("listPosMax") ?: 0L

        setDisplayHomeAsUpEnabled(true)

        // provide (injected) viewModel as data source for data binding
        binding.viewModel = _viewModel

        // install listener for SwipeRefreshLayout view
        binding.refreshLayout.setOnRefreshListener {

            // deactivate SwipeRefreshLayout spinner
            binding.refreshLayout.setRefreshing(false)

            // refresh local DB data from backend (for this list) - also updates 'showNoData'
            _viewModel.swipeRefreshDataInLocalDB()

            // empty? --> inform user that there is no point swiping for further updates...
            if (_viewModel.showNoData.value == true) {
                Toast.makeText(activity, getString(R.string.error_add_smob_users), Toast.LENGTH_SHORT).show()
            }

        }

        // refresh local DB data from backend (for this list) - also updates 'showNoData'
        _viewModel.swipeRefreshDataInLocalDB()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // RV - incl. onClick listener for items
        setupRecyclerView()


        // clicking on the 'saveSmobGroup' FAB updates the smobGroup with the newly added member
        binding.saveSmobGroup.setOnClickListener {

            // close SoftKeyboard
            closeSoftKeyboard(requireContext(), view)

            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.GERMANY)
            val currentDate = sdf.format(Date())

            // TODO: need to "update" existing entry (rather than adding a new one)
            // TODO: need to "update" existing entry (rather than adding a new one)
            // TODO: need to "update" existing entry (rather than adding a new one)

            // initialize data record to be written to DB
            // ... if no better values have been provided by the user (taken from viewModel), this
            //     is going to be the data record written to the DB
            daSmobGroupATO = SmobGroupATO(
                UUID.randomUUID().toString(),
                SmobItemStatus.OPEN,
                listPosMax + 1,
                _viewModel.smobGroupName.value ?: "mystery group",
                _viewModel.smobGroupDescription.value ?: "something exciting",
                _viewModel.smobGroupType.value ?: GroupType.OTHER,
                SmobApp.currUser?.let { listOf(it.id) } ?: listOf(), // members
                ActivityStatus(currentDate, 0)
            )

            // store smob Group in DB
            // ... this also takes the user back to the SmobGroupsFragment
            _viewModel.validateAndSaveSmobGroup(daSmobGroupATO)

        }  // onClickListener (FAB - save)

    }  // onViewCreated


    private fun setupRecyclerView() {
        val adapter = AdminGroupMembersAdapter(binding.root) {

            // this lambda is the 'callback' function which gets called when clicking an item in the
            // RecyclerView - it gets the data behind the clicked item as parameter

            // communicate the selected item (= member)
            _viewModel.currGroupMember = it
            _viewModel.enableAddButton = true

            // use the navigationCommand live data to navigate between the fragments
            _viewModel.navigationCommand.postValue(
                NavigationCommand.To(
                    AdminGroupMembersEditFragmentDirections
                        .actionSmobAdminGroupMembersEditFragmentToSmobAdminGroupMemberDetailsFragment()
                )
            )

        }  // "on-item-click" lambda

        // connect SearchView to RecyclerView
        binding.smobGroupMemberSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

        // setup the recycler view using the extension function
        binding.smobItemsRecyclerView.setup(adapter)

        // enable swiping left/right
        val itemTouchHelper = ItemTouchHelper(AdminGroupMembersSwipeActionHandler(adapter))
        itemTouchHelper.attachToRecyclerView(binding.smobItemsRecyclerView)

    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClearGroup()
    }

}
