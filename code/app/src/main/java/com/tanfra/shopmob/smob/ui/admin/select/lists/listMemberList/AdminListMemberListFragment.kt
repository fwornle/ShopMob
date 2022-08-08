package com.tanfra.shopmob.smob.ui.admin.select.lists.listMemberList

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setTitle
import android.widget.Toast
import androidx.core.os.bundleOf
import org.koin.core.component.KoinComponent
import timber.log.Timber
import androidx.recyclerview.widget.ItemTouchHelper
import com.tanfra.shopmob.databinding.FragmentAdminListMemberListBinding
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import com.tanfra.shopmob.utils.setup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class AdminListMemberListFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the ViewModel instance
    override val _viewModel: AdminViewModel by sharedViewModel()

    // data binding for fragment_admin_group_member_list.xml
    private lateinit var binding: FragmentAdminListMemberListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_admin_group_member_list, container, false
            )

        // set injected viewModel (from KOIN service locator)
        binding.viewModel = _viewModel

//        // fetch ID of group to be displayed (from incoming bundle)
//        val groupId = arguments?.getString("groupId")
//        val groupName = arguments?.getString("groupName")

        // fetch group ID of the (clicked) group that got us here
        val groupId = _viewModel.currGroup?.id
        val groupName = _viewModel.currGroup?.name

        // console
        Timber.i("Showing members of group with ID: $groupId")
        Timber.i("... group name: $groupName")

        // register flows fetch items of the selected upstream group (as well as the group itself)
        groupId?.let {

            // set current group ID and group(list)position in viewModel
            _viewModel.currGroupId = it

            // fetch flow into new (alternative) StateFlow variable smobGroup2
            // ... this just hooks up the (cold) Room flow to the StateFlow variable - no collection
            _viewModel.fetchSmobGroup()

            // register flows in viewModel
            _viewModel._smobGroup = _viewModel.fetchSmobGroupFlow(it)  // holds the item 'status'
            _viewModel._smobGroupMembers = _viewModel.fetchSmobGroupMembersFlow(it)

            // turn to StateFlows
            _viewModel.smobGroup = _viewModel.smobGroupFlowToStateFlow(_viewModel._smobGroup)
            _viewModel.smobGroupMembers = _viewModel.smobGroupMembersFlowToStateFlow(_viewModel._smobGroupMembers)

            // combine the flows and turn into StateFlow
            _viewModel.smobGroupMemberWithGroupData = _viewModel.combineGroupFlowsAndConvertToStateFlow(
                _viewModel._smobGroup,
                _viewModel._smobGroupMembers,
            )

        }

        // configure navbar
        setDisplayHomeAsUpEnabled(true)
        setTitle(String.format(getString(R.string.app_name_admin_members), groupName))

        // install listener for SwipeRefreshLayout view
        binding.refreshLayout.setOnRefreshListener {

            // deactivate SwipeRefreshLayout spinner
            binding.refreshLayout.setRefreshing(false)

            // refresh local DB data from backend (for this list) - also updates 'showNoData'
            _viewModel.swipeRefreshUserDataInLocalDB()

            // empty? --> inform user that there is no point swiping for further updates...
            if (_viewModel.showNoData.value == true) {
                Toast.makeText(activity, getString(R.string.error_add_smob_users), Toast.LENGTH_SHORT).show()
            }

        }

        // refresh local DB data from backend (for this list) - also updates 'showNoData'
        _viewModel.swipeRefreshUserDataInLocalDB()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // RV - incl. onClick listener for items
        setupRecyclerView()

        // "+" FAB
        binding.addSmobItemFab.setOnClickListener { navigateToAdminAddGroupMember() }

    }

    // FAB handler --> navigate to AdminUserEdit fragment
    private fun navigateToAdminAddGroupMember() {

        // determine highest index of all members in currently selected group
        val highPos = _viewModel.currGroup.let {
            it?.members?.fold(0L) { max, list ->
                if (list.listPosition > max) list.listPosition
                else max
            } ?: 0L
        }

        // communicate the currently highest list position
        val bundle = bundleOf(
            "listPosMax" to highPos,
        )

        // use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.ToWithBundle(
                R.id.smobAdminListMemberSelectFragment,
                bundle
            )
        )

    }

    private fun setupRecyclerView() {

        val adapter = AdminListMemberListAdapter(binding.root) {

            // this lambda is the 'callback' function which gets called when clicking an item in the
            // RecyclerView - it gets the data behind the clicked item as parameter

            // communicate the selected item (= member)
            _viewModel.currListMemberWithListData = it
            _viewModel.currMemberDetails = it.member()  // used in details display

            // use the navigationCommand live data to navigate between the fragments
            _viewModel.navigationCommand.postValue(
                NavigationCommand.To(
                    AdminListMemberListFragmentDirections
                        .actionSmobAdminListMemberListFragmentToSmobAdminListMemberDetailsFragment()
                )
            )

        }  // "on-item-click" lambda

        // setup the recycler view using the extension function
        binding.smobItemsRecyclerView.setup(adapter)

        // enable swiping left/right (comment out to disable swiping)
        val itemTouchHelper = ItemTouchHelper(AdminListMemberListSwipeActionHandler(adapter))
        itemTouchHelper.attachToRecyclerView(binding.smobItemsRecyclerView)

    }

}