package com.tanfra.shopmob.smob.ui.admin.lists.listGroupSelect

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.FragmentAdminListGroupSelectBinding
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseFragment
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationCommand
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setup
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent
import timber.log.Timber


class AdminListGroupSelectFragment : BaseFragment(), KoinComponent {

    // get the view model (from Koin service locator) ... shared with PlanningListsFragment
    override val viewModel: AdminViewModel by activityViewModel()

    // data binding of underlying layout
    private lateinit var binding: FragmentAdminListGroupSelectBinding

    // new lists are created at the highest position (+1)
    private var listPosMax: Long = 0L


    // create fragment view
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // inflate fragment layout and return binding object
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_admin_list_group_select,
                container,
                false
            )

        // fetch currently highest list position from incoming bundle
        listPosMax = arguments?.getLong("listPosMax") ?: 0L

        // fetch list ID of the (clicked) list that got us here
        val listId = viewModel.currList?.id
        val listName = viewModel.currList?.name

        // console
        Timber.i("Adding new group to list with ID: $listId")
        Timber.i("List name: $listName")

        // register flows, collect items of the selected upstream list (as well as the list itself)
        listId?.let {

            // register (not yet collected) flow / StateFlow in viewModel:
            viewModel.allSmobGroupsF = viewModel.registerAllSmobGroupsFlow()

            // combine the flows and turn into StateFlow
            viewModel.smobAllGroupsWithListDataSF = viewModel.combineAllGroupsAndListFlowSF(
                viewModel.smobListF,
                viewModel.allSmobGroupsF,
            )

        }

        setDisplayHomeAsUpEnabled(true)

        // provide (injected) viewModel as data source for data binding
        binding.viewModel = viewModel

        // install listener for SwipeRefreshLayout view
        binding.refreshLayout.setOnRefreshListener {

            // deactivate SwipeRefreshLayout spinner
            binding.refreshLayout.setRefreshing(false)

            // refresh local DB data from backend (for this list) - also updates 'showNoData'
            viewModel.swipeRefreshGroupDataInLocalDB()

            // empty? --> inform user that there is no point swiping for further updates...
            if (viewModel.showNoData.value == true) {
                Toast.makeText(activity, getString(R.string.error_add_smob_users), Toast.LENGTH_SHORT).show()
            }

        }

        // refresh local DB data from backend (for this list) - also updates 'showNoData'
        viewModel.swipeRefreshGroupDataInLocalDB()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // RV - incl. onClick listener for items
        setupRecyclerView()

    }  // onViewCreated


    private fun setupRecyclerView() {

        val adapter = AdminListGroupSelectAdapter(binding.root) {

            // this lambda is the 'callback' function which gets called when clicking an item in the
            // RecyclerView - it gets the data behind the clicked item as parameter

            // communicate the selected item (= group)
            viewModel.currGroupWithListData = it
            viewModel.currGroupDetail = it.group()  // used in details display
            viewModel.enableAddButton = true

            // use the navigationCommand live data to navigate between the fragments
            viewModel.navigationCommand.postValue(
                NavigationCommand.To(
                    AdminListGroupSelectFragmentDirections
                        .actionSmobAdminListGroupSelectFragmentToSmobAdminListGroupDetailsFragment()
                )
            )

        }  // "on-item-click" lambda

        // connect SearchView to RecyclerView
        binding.smobGroupItemSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
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

        // enable swiping left/right (comment out to disable swiping)
//        val itemTouchHelper = ItemTouchHelper(AdminListGroupSelectSwipeActionHandler(adapter))
//        itemTouchHelper.attachToRecyclerView(binding.smobItemsRecyclerView)

    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        viewModel.onClearGroup()
    }

}
