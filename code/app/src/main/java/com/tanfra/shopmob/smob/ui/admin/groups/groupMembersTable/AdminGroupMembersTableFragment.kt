package com.tanfra.shopmob.smob.ui.admin.groups.groupMembersTable

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseFragment
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationCommand
import com.tanfra.shopmob.app.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.app.utils.setTitle
import android.widget.Toast
import androidx.core.os.bundleOf
import org.koin.core.component.KoinComponent
import timber.log.Timber
import androidx.recyclerview.widget.ItemTouchHelper
import com.tanfra.shopmob.databinding.FragmentAdminGroupMembersTableBinding
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import com.tanfra.shopmob.app.utils.setup
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class AdminGroupMembersTableFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the ViewModel instance
    override val viewModel: AdminViewModel by activityViewModel()

    // data binding for fragment_admin_group_member_list.xml
    private lateinit var binding: FragmentAdminGroupMembersTableBinding

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_admin_group_members_table, container, false
            )

        // set injected viewModel (from KOIN service locator)
        binding.viewModel = viewModel

//        // fetch ID of group to be displayed (from incoming bundle)
//        val groupId = arguments?.getString("groupId")
//        val groupName = arguments?.getString("groupName")

        // fetch group ID of the (clicked) group that got us here
        val groupId = viewModel.currGroup?.id
        val groupName = viewModel.currGroup?.name

        // console
        Timber.i("Showing members of group with ID: $groupId")
        Timber.i("... group name: $groupName")

        // register flows fetch items of the selected upstream group (as well as the group itself)
        groupId?.let {

            // set current group ID and group(list)position in viewModel
            viewModel.currGroupId = it

//            // fetch flow into new (alternative) StateFlow variable (_)smobGroupAltSF
//            // ... this just hooks up the (cold) Room flow to the StateFlow variable - no collection
//            viewModel.collectSmobGroupAltSF()

            // register flows in viewModel
            viewModel.smobGroupF = viewModel.registerSmobGroupFlow(it)  // holds the item 'status'
            viewModel.smobGroupMembersF = viewModel.registerSmobGroupMembersFlow(it)

            // turn to StateFlows
            viewModel.smobGroupSF = viewModel.smobGroupFlowAsSF(viewModel.smobGroupF)
            viewModel.smobGroupMembersSF = viewModel.smobGroupMembersFlowToSF(viewModel.smobGroupMembersF)

            // combine the flows and turn into StateFlow
            viewModel.smobGroupMemberWithGroupDataSF = viewModel.combineGroupAndUserFlowsSF(
                viewModel.smobGroupF,
                viewModel.smobGroupMembersF,
            )

        }

        // configure navbar
        setDisplayHomeAsUpEnabled(true)
        setTitle(String.format(getString(R.string.app_name_admin_group_members), groupName))

        // install listener for SwipeRefreshLayout view
        binding.refreshLayout.setOnRefreshListener {

            // deactivate SwipeRefreshLayout spinner
            binding.refreshLayout.isRefreshing = false

            // refresh local DB data from backend (for this list) - also updates 'showNoData'
            viewModel.swipeRefreshUserDataInLocalDB()

            // empty? --> inform user that there is no point swiping for further updates...
            if (viewModel.showNoData.value == true) {
                Toast.makeText(activity, getString(R.string.error_add_smob_users), Toast.LENGTH_SHORT).show()
            }

        }

        // refresh local DB data from backend (for this list) - also updates 'showNoData'
        viewModel.swipeRefreshUserDataInLocalDB()

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
        val highPos = viewModel.currGroup.let {
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
        viewModel.navigationCommand.postValue(
            NavigationCommand.ToWithBundle(
                R.id.smobAdminGroupMemberSelectFragment,
                bundle
            )
        )

    }

    private fun setupRecyclerView() {

        val adapter = AdminGroupMembersTableAdapter(binding.root) {

            // this lambda is the 'callback' function which gets called when clicking an item in the
            // RecyclerView - it gets the data behind the clicked item as parameter

            // communicate the selected item (= group member)
            viewModel.currGroupMemberWithGroupData = it
            viewModel.currGroupMember = it.member()  // used in details display

            // use the navigationCommand live data to navigate between the fragments
            viewModel.navigationCommand.postValue(
                NavigationCommand.To(
                    AdminGroupMembersTableFragmentDirections
                        .actionSmobAdminGroupMembersTableFragmentToSmobAdminGroupMemberDetailsFragment()
                )
            )

        }  // "on-item-click" lambda

        // setup the recycler view using the extension function
        binding.smobItemsRecyclerView.setup(adapter)

        // enable swiping left/right (comment out to disable swiping)
        val itemTouchHelper = ItemTouchHelper(AdminGroupMembersTableSwipeActionHandler(adapter))
        itemTouchHelper.attachToRecyclerView(binding.smobItemsRecyclerView)

    }

}