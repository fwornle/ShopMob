package com.tanfra.shopmob.smob.ui.admin.lists.listGroupsTable

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
import com.tanfra.shopmob.databinding.FragmentAdminListGroupsTableBinding
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import com.tanfra.shopmob.utils.setup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class AdminListGroupsTableFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the ViewModel instance
    override val _viewModel: AdminViewModel by sharedViewModel()

    // data binding for fragment_admin_group_member_list.xml
    private lateinit var binding: FragmentAdminListGroupsTableBinding

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
        binding.viewModel = _viewModel

        // fetch list ID of the (clicked) list that got us here
        val listId = _viewModel.currList?.id
        val listName = _viewModel.currList?.name

        // console
        Timber.i("Showing groups/members of list with ID: $listId")
        Timber.i("List name: $listName")

        // register flows, collect items of the selected upstream list (as well as the list itself)
        listId?.let {

            // register (not yet collected) flow / StateFlow in viewModel:
            // --> selected SmobList
            _viewModel.smobListF = _viewModel.registerSmobListFlow(it)
            _viewModel.smobListSF = _viewModel.registerSmobListFlowAsStateFlow(_viewModel.smobListF)

//            // collect flow of selected SmobList into (ALTERNATIVE) StateFlow variable
//            // ... this variant (currListAltSF) is still Resource wrapped
//            _viewModel.collectSmobListItemAsAltSF()

            // register (not yet collected) flow / StateFlow in viewModel:
            // --> referenced Groups of selected SmobList
            _viewModel.smobListGroupsF = _viewModel.registerSmobListGroupsFlow(it)
            _viewModel.smobListGroupsSF = _viewModel.smobListGroupsFlowAsSF(_viewModel.smobListGroupsF)

            // combine the flows and turn into StateFlow
            _viewModel.smobListGroupsWithListDataSF = _viewModel.combineListGroupsAndListFlowSF(
                _viewModel.smobListF,
                _viewModel.smobListGroupsF,
            )

        }

        // configure navbar
        setDisplayHomeAsUpEnabled(true)
        setTitle(String.format(getString(R.string.app_name_admin_groups_in_list), listName))

        // install listener for SwipeRefreshLayout view
        binding.refreshLayout.setOnRefreshListener {

            // deactivate SwipeRefreshLayout spinner
            binding.refreshLayout.setRefreshing(false)

            // refresh local DB data from backend (for this list) - also updates 'showNoData'
            _viewModel.swipeRefreshListDataInLocalDB()

            // empty? --> inform user that there is no point swiping for further updates...
            if (_viewModel.showNoData.value == true) {
                Toast.makeText(activity, getString(R.string.error_add_smob_groups), Toast.LENGTH_SHORT).show()
            }

        }

        // refresh local DB data from backend (for this list) - also updates 'showNoData'
        _viewModel.swipeRefreshListDataInLocalDB()

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
        val highPos = _viewModel.currList.let {
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
        _viewModel.navigationCommand.postValue(
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

            // communicate the selected item (= member)
            _viewModel.currGroupWithListData = it
            _viewModel.currGroupDetail = it.group()  // used in details display

            // use the navigationCommand live data to navigate between the fragments
            _viewModel.navigationCommand.postValue(
                NavigationCommand.To(
                    AdminListGroupsTableFragmentDirections
                        .actionSmobAdminListGroupsTableFragmentToSmobAdminListGroupDetailsFragment()
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