package com.tanfra.shopmob.smob.ui.admin.select.groups.groupMemberSelect

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.FragmentAdminGroupMemberSelectBinding
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinComponent


class AdminGroupMemberSelectFragment : BaseFragment(), KoinComponent {

    // get the view model (from Koin service locator) ... shared with PlanningListsFragment
    override val _viewModel: AdminViewModel by sharedViewModel()

    // data binding of underlying layout
    private lateinit var binding: FragmentAdminGroupMemberSelectBinding

    // new lists are created at the highest position (+1)
    private var listPosMax: Long = 0L


    // create fragment view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // inflate fragment layout and return binding object
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_admin_group_member_select,
                container,
                false
            )

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

    }  // onViewCreated


    private fun setupRecyclerView() {

        val adapter = AdminGroupMemberSelectAdapter(binding.root) {

            // this lambda is the 'callback' function which gets called when clicking an item in the
            // RecyclerView - it gets the data behind the clicked item as parameter

            // communicate the selected item (= member)
            _viewModel.currGroupMember = it
            _viewModel.currMemberDetails = it.member()  // used in details display
            _viewModel.enableAddButton = true

            // use the navigationCommand live data to navigate between the fragments
            _viewModel.navigationCommand.postValue(
                NavigationCommand.To(
                    AdminGroupMemberSelectFragmentDirections
                        .actionSmobAdminGroupMemberSelectFragmentToSmobAdminGroupMemberDetailsFragment()
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

        // enable swiping left/right (comment out to disable swiping)
//        val itemTouchHelper = ItemTouchHelper(AdminGroupMembersSwipeActionHandler(adapter))
//        itemTouchHelper.attachToRecyclerView(binding.smobItemsRecyclerView)

    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClearGroup()
    }

}
