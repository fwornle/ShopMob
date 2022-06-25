package com.tanfra.shopmob.smob.ui.admin.groupMembers

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setTitle
import android.content.Intent
import android.widget.Toast
import androidx.core.os.bundleOf
import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.smob.ui.auth.SmobAuthActivity
import org.koin.core.component.KoinComponent
import timber.log.Timber
import androidx.recyclerview.widget.ItemTouchHelper
import com.tanfra.shopmob.databinding.FragmentAdminGroupMembersBinding
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import com.tanfra.shopmob.utils.setup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class AdminGroupMembersListFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the ViewModel instance
    override val _viewModel: AdminViewModel by sharedViewModel()

    // data binding for fragment_smob_planning_lists.xml
    private lateinit var binding: FragmentAdminGroupMembersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_admin_group_members, container, false
            )

        // set injected viewModel (from KOIN service locator)
        binding.viewModel = _viewModel

        // fetch ID of group to be displayed (from incoming bundle)
        val groupId = arguments?.getString("groupId")
        Timber.i("Showing members of group with ID: $groupId")

        val groupName = arguments?.getString("groupName")
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
            _viewModel.smobGroupMembersWithStatus = _viewModel.combineFlowsAndConvertToStateFlow(
                _viewModel._smobGroup,
                _viewModel._smobGroupMembers,
            )

        }

        // configure navbar
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        setTitle(String.format(getString(R.string.app_name_planning), groupName))

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
        binding.addSmobItemFab.setOnClickListener { navigateToAdminUserEdit() }

    }

    // FAB handler --> navigate to AdminUserEdit fragment
    private fun navigateToAdminUserEdit() {

//        // determine hightest index in all smobLists
//        val highPos = _viewModel.smobGroupMembers.value.let {
//            if (it.status == Status.SUCCESS) {
//                // return  highest index
//                it.data?.fold(0L) { max, list -> if (list.itemPosition > max) list.itemPosition else max } ?: 0L
//            } else {
//                0L
//            }
//        }
//
//        // communicate the currently highest list position
//        val bundle = bundleOf(
//            "listPosMax" to highPos,
//        )
//
//        // use the navigationCommand live data to navigate between the fragments
//        _viewModel.navigationCommand.postValue(
//            NavigationCommand.ToWithBundle(
//                R.id.smobAdminGroupMembersEditFragment,
//                bundle
//            )
//        )

        // use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                AdminGroupMembersListFragmentDirections.actionSmobAdminGroupMembersListFragmentToSmobAdminGroupMembersEditFragment()
            )
        )
    }

    private fun setupRecyclerView() {
        val adapter = AdminGroupMembersAdapter(binding.root) {

            // this lambda is the 'callback' function which gets called when clicking an item in the
            // RecyclerView - it gets the data behind the clicked item as parameter

            // communicate the selected item (= member)
            _viewModel.currGroupMember = it

            // use the navigationCommand live data to navigate between the fragments
            _viewModel.navigationCommand.postValue(
                NavigationCommand.To(
                    AdminGroupMembersListFragmentDirections
                        .actionSmobAdminGroupMembersListFragmentToSmobAdminGroupMemberDetailsFragment()
                )
            )

        }  // "on-item-click" lambda

        // setup the recycler view using the extension function
        binding.smobItemsRecyclerView.setup(adapter)

        // enable swiping left/right
        val itemTouchHelper = ItemTouchHelper(AdminGroupMembersSwipeActionHandler(adapter))
        itemTouchHelper.attachToRecyclerView(binding.smobItemsRecyclerView)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                // logout authenticated user
                AuthUI.getInstance()
                    .signOut(this.requireContext())
                    .addOnCompleteListener {
                        // user is now signed out -> redirect to login screen
                        startActivity(Intent(this.context, SmobAuthActivity::class.java))
                        // and we're done here
                        this.activity?.finish()
                    }
            }
        }  // when(item...)

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        // display logout as menu item
        inflater.inflate(R.menu.main_menu, menu)
    }

}