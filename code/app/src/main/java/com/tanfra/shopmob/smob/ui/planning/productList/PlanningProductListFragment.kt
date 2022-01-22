package com.tanfra.shopmob.smob.ui.planning.productList

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
import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.smob.ui.authentication.SmobAuthenticationActivity
import com.tanfra.shopmob.smob.ui.details.SmobDetailsActivity
import com.tanfra.shopmob.smob.ui.details.SmobDetailsSources
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import org.koin.core.component.KoinComponent
import timber.log.Timber
import androidx.recyclerview.widget.ItemTouchHelper
import com.tanfra.shopmob.databinding.FragmentPlanningProductListBinding
import com.tanfra.shopmob.utils.setup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class PlanningProductListFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the ViewModel instance
    override val _viewModel: PlanningProductListViewModel by sharedViewModel()

    // data binding for fragment_smob_planning_lists.xml
    private lateinit var binding: FragmentPlanningProductListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_planning_product_list, container, false
            )

        // set injected viewModel (from KOIN service locator)
        binding.viewModel = _viewModel

        // fetch ID of list to be displayed (from incoming bundle)
        val listId = arguments?.getString("listId")
        Timber.i("Showing products on list with ID: $listId")

        val listName = arguments?.getString("listName")
        Timber.i("... list name: $listName")

        // register flows fetch items of the selected upstream list (as well as the list itself)
        listId?.let {

            // set current list ID and listPosition in viewModel
            _viewModel.currListId = it

            // register flows in viewModel
            _viewModel._smobList = _viewModel.fetchSmobListFlow(it)  // holds the item 'status'
            _viewModel._smobListItems = _viewModel.fetchSmobListItemsFlow(it)

            // turn to StateFlows
            _viewModel.smobList = _viewModel.smobListFlowToStateFlow(_viewModel._smobList)
            _viewModel.smobListItems = _viewModel.smobListItemsFlowToStateFlow(_viewModel._smobListItems)

            // combine the flows and turn into StateFlow
            _viewModel.smobListItemsWithStatus = _viewModel.combineFlowsAndConvertToStateFlow(
                _viewModel._smobList,
                _viewModel._smobListItems,
            )

//            // collect flows and store in StateFlow type (so that we have the latest value available
//            _viewModel.fetchSmobList()
//            _viewModel.fetchSmobListItems()
//
//            // combine the flows and turn into StateFlow
//            _viewModel.fetchCombinedFlows()

        }

        // configure navbar
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        setTitle(String.format(getString(R.string.app_name_planning), listName))

        // install listener for SwipeRefreshLayout view
        binding.refreshLayout.setOnRefreshListener {

            // deactivate SwipeRefreshLayout spinner
            binding.refreshLayout.setRefreshing(false)

            // refresh local DB data from backend (for this list) - also updates 'showNoData'
            _viewModel.swipeRefreshProductDataInLocalDB()

            // empty? --> inform user that there is no point swiping for further updates...
            if (_viewModel.showNoData.value == true) {
                Toast.makeText(activity, getString(R.string.error_add_smob_items), Toast.LENGTH_SHORT).show()
            }

        }

        // refresh local DB data from backend (for this list) - also updates 'showNoData'
        _viewModel.swipeRefreshProductDataInLocalDB()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // RV - incl. onClick listener for items
        setupRecyclerView()

        // "+" FAB
        binding.addSmobItemFab.setOnClickListener { navigateToPlanningProductEdit() }

    }

    // FAB handler --> navigate to PlanningProductEdit fragment
    private fun navigateToPlanningProductEdit() {
        // use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                PlanningProductListFragmentDirections.actionPlanningProductListFragmentToPlanningProductEditFragment()
            )
        )
    }

    private fun setupRecyclerView() {
        val adapter = PlanningProductListAdapter(binding.root) {

            // this lambda is the 'callback' function which gets called when clicking an item in the
            // RecyclerView - it gets the data behind the clicked item as parameter

            // create intent which starts activity SmobDetailsActivity, with clicked data item
            val intent = SmobDetailsActivity.newIntent(
                requireContext(),
                SmobDetailsSources.PLANNING_PRODUCT_LIST,
                it
            )

            wrapEspressoIdlingResource {
                startActivity(intent)
            }

        }  // "on-item-click" lambda

        // setup the recycler view using the extension function
        binding.smobItemsRecyclerView.setup(adapter)

        // enable swiping left/right
        val itemTouchHelper = ItemTouchHelper(PlanningProductListSwipeActionHandler(adapter))
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
                        startActivity(Intent(this.context, SmobAuthenticationActivity::class.java))
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