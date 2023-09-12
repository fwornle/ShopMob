package com.tanfra.shopmob.smob.ui.admin.lists.listsTable

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseFragment
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setTitle
import android.content.Intent
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.ItemTouchHelper
import com.tanfra.shopmob.databinding.FragmentAdminListsTableBinding
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationCommand
import com.tanfra.shopmob.smob.ui.planning.SmobPlanningActivity
import com.tanfra.shopmob.utils.setup
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent
import timber.log.Timber

class AdminListsTableFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the ViewModel instance
    override val viewModel: AdminViewModel by activityViewModel()

    // data binding for fragment_planning_lists.xml
    private lateinit var binding: FragmentAdminListsTableBinding

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_admin_lists_table, container, false
            )

        // set injected viewModel (from KOIN service locator)
        binding.viewModel = viewModel

        setDisplayHomeAsUpEnabled(true)
        setTitle(getString(R.string.app_name_admin_lists))

        // install listener for SwipeRefreshLayout view
        binding.refreshLayout.setOnRefreshListener {

            // deactivate SwipeRefreshLayout spinner
            binding.refreshLayout.isRefreshing = false

            // refresh local DB data from backend (for this list) - also updates 'showNoData'
            viewModel.swipeRefreshGroupDataInLocalDB()

            // empty? --> inform user that there is no point swiping for further updates...
            if (viewModel.showNoData.value == true) {
                Toast.makeText(activity, getString(R.string.error_add_smob_lists), Toast.LENGTH_SHORT).show()
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

        // handlers for "+" FAB
        binding.addSmobItemFab.setOnClickListener { navigateToAddList() }


        // set onClick handler for DISMISS button
        // ... navigate back to the main app
        binding.btDismiss.setOnClickListener {
            val intent = Intent(this.context, SmobPlanningActivity::class.java)
            startActivity(intent)
            // and we're done here
            this.activity?.finish()
        }

    }


    private fun setupRecyclerView() {
        val adapter = AdminListsTableAdapter(binding.root) {

            // this lambda is the 'callback' function which gets called when clicking an item in the
            // RecyclerView - it gets the data behind the clicked item as parameter

            // store currently selected list in viewModel
            viewModel.currList = it

            // use the navigationCommand live data to navigate between the fragments
            viewModel.navigationCommand.postValue(
                NavigationCommand.To(
                    AdminListsTableFragmentDirections
                        .actionSmobAdminListsTableFragmentToSmobAdminListGroupsTableFragment()
                )
            )

        }  // "on-item-click" lambda

        // setup the recycler view using the extension function
        binding.smobItemsRecyclerView.setup(adapter)

        // enable swiping left/right
        val itemTouchHelper = ItemTouchHelper(AdminListsTableSwipeActionHandler(adapter))
        itemTouchHelper.attachToRecyclerView(binding.smobItemsRecyclerView)

    }


    // "+" FAB handler --> navigate to selected fragment of the admin activity
    private fun navigateToAddList() {

        // determine highest index in all smobLists
        val highPos = viewModel.smobListsSF.value.let {
            when (it) {
                is Resource.Error -> Timber.i("Couldn't retrieve SmobList from remote")
                is Resource.Loading -> Timber.i("SmobList still loading")
                is Resource.Success -> {
                    it.data.let { daList ->
                        daList.fold(0L) { max, list ->
                            if (list.position > max) list.position else max
                        }
                    }
                }  // Resource.Success
            }  // when
        }

        // communicate the currently highest list position
        val bundle = bundleOf(
            "listPosMax" to highPos,
        )

        // use the navigationCommand live data to navigate between the fragments
        viewModel.navigationCommand.postValue(
            NavigationCommand.ToWithBundle(
                R.id.smobAdminListsAddNewItemFragment,
                bundle
            )
        )

    }

}