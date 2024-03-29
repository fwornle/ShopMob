package com.tanfra.shopmob.smob.ui.admin.lists.listGroupsTable

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.FragmentAdminListGroupsTableBinding
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseFragment
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationCommand
import com.tanfra.shopmob.app.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.app.utils.setTitle
import com.tanfra.shopmob.app.utils.setup
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent
import timber.log.Timber


class AdminListGroupsTableFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the ViewModel instance
    override val viewModel: AdminViewModel by activityViewModel()

    // data binding for fragment_admin_group_member_list.xml
    private lateinit var binding: FragmentAdminListGroupsTableBinding

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_admin_list_groups_table, container, false
            )

        // set injected viewModel (from KOIN service locator)
        binding.viewModel = viewModel

        // reset backDestinationId
        viewModel.backDestinationId = null

        // fetch list ID of the (clicked) list that got us here
        val listId = viewModel.currList?.id
        val listName = viewModel.currList?.name

        // console
        Timber.i("Showing groups/members of list with ID: $listId")
        Timber.i("List name: $listName")

        // register flows, collect items of the selected upstream list (as well as the list itself)
        listId?.let {

            // register (not yet collected) flow / StateFlow in viewModel:
            // --> selected SmobList
            viewModel.smobListF = viewModel.registerSmobListFlow(it)
            viewModel.smobListSF = viewModel.registerSmobListFlowAsStateFlow(viewModel.smobListF)

            // register (not yet collected) flow / StateFlow in viewModel:
            // --> referenced Groups of selected SmobList
            viewModel.smobListGroupsF = viewModel.registerSmobListGroupsFlow(it)
            viewModel.smobListGroupsSF = viewModel.smobListGroupsFlowAsSF(viewModel.smobListGroupsF)

            // combine the flows and turn into StateFlow
            viewModel.smobListGroupsWithListDataSF = viewModel.combineListGroupsAndListFlowSF(
                viewModel.smobListF,
                viewModel.smobListGroupsF,
            )

        }

        // configure navbar
        setDisplayHomeAsUpEnabled(true)
        setTitle(String.format(getString(R.string.app_name_admin_groups_in_list), listName))

        // install listener for SwipeRefreshLayout view
        binding.refreshLayout.setOnRefreshListener {

            // deactivate SwipeRefreshLayout spinner
            binding.refreshLayout.isRefreshing = false

            // refresh local DB data from backend (for this list) - also updates 'showNoData'
            viewModel.swipeRefreshListDataInLocalDB()

            // empty? --> inform user that there is no point swiping for further updates...
            if (viewModel.showNoData.value == true) {
                Toast.makeText(activity, getString(R.string.error_add_smob_groups), Toast.LENGTH_SHORT).show()
            }

        }

        // refresh local DB data from backend (for this list) - also updates 'showNoData'
        viewModel.swipeRefreshListDataInLocalDB()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // RV - incl. onClick listener for items
        setupRecyclerView()

        // "+" FAB
        binding.addSmobItemFab.setOnClickListener { navigateToAdminAddGroupToList() }

    }

    // FAB handler --> navigate to AdminListGroupsAddNewItem fragment
    private fun navigateToAdminAddGroupToList() {

        // determine highest index of all groups in currently selected list
        val highPos = viewModel.currList.let {
            it?.groups?.fold(0L) { max, group ->
                if (group.listPosition > max) group.listPosition
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
                R.id.smobAdminListGroupSelectFragment,
                bundle
            )
        )

    }

    private fun setupRecyclerView() {

        val adapter = AdminListGroupsTableAdapter(binding.root) {

            // this lambda is the 'callback' function which gets called when clicking an item in the
            // RecyclerView - it gets the data behind the clicked item as parameter

//            // communicate the selected item (= member)
//            viewModel.currGroupWithListData = it
//            viewModel.currGroupDetail = it.group()  // used in details display
//
//            // use the navigationCommand live data to navigate between the fragments
//            viewModel.navigationCommand.postValue(
//                NavigationCommand.To(
//                    AdminListGroupsTableFragmentDirections
//                        .actionSmobAdminListGroupsTableFragmentToSmobAdminListGroupDetailsFragment()
//                )
//            )

            // store currently selected group in viewModel
            viewModel.currGroup = it.group()

            // set back address (to return to this fragment, if we came from here)
            viewModel.backDestinationId = R.id.smobAdminListGroupsTableFragment
            Timber.i("Setting 'backDestinationId' to smobAdminListGroupsTableFragment: ${viewModel.backDestinationId}")

            // use the navigationCommand live data to navigate between the fragments
            viewModel.navigationCommand.postValue(
                NavigationCommand.To(
                    AdminListGroupsTableFragmentDirections
                        .actionSmobAdminListGroupsTableFragmentToSmobAdminGroupMembersTableFragment()
                )
            )

        }  // "on-item-click" lambda

        // setup the recycler view using the extension function
        binding.smobItemsRecyclerView.setup(adapter)

        // enable swiping left/right (comment out to disable swiping)
        val itemTouchHelper = ItemTouchHelper(AdminListGroupsTableSwipeActionHandler(adapter))
        itemTouchHelper.attachToRecyclerView(binding.smobItemsRecyclerView)

    }

}