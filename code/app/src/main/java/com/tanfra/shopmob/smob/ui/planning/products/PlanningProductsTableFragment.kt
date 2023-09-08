package com.tanfra.shopmob.smob.ui.planning.products

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.FragmentPlanningProductsTableBinding
import com.tanfra.shopmob.smob.ui.auth.SmobAuthActivity
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseFragment
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationCommand
import com.tanfra.shopmob.smob.ui.details.SmobDetailsActivity
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationSource
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setTitle
import com.tanfra.shopmob.utils.setup
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent
import timber.log.Timber


class PlanningProductsTableFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the ViewModel instance
    override val viewModel: PlanningViewModel by activityViewModel()

    // data binding for fragment_smob_planning_lists.xml
    private lateinit var binding: FragmentPlanningProductsTableBinding

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_planning_products_table, container, false
            )

        // set injected viewModel (from KOIN service locator)
        binding.viewModel = viewModel

        // fetch ID of list to be displayed (from incoming bundle)
        val listId = arguments?.getString("listId")
        Timber.i("Showing products on list with ID: $listId")

        val listName = arguments?.getString("listName")
        Timber.i("... list name: $listName")

        // register flows fetch items of the selected upstream list (as well as the list itself)
        listId?.let {

            // set current list ID and listPosition in viewModel
            viewModel.currListId = it

            // fetch flow into new (alternative) StateFlow variable smobListStaticMSF
            // ... this just hooks up the (cold) Room flow to the StateFlow variable - no collection
            viewModel.collectSmobList()

            // register flows in viewModel
            viewModel.smobListF = viewModel.getFlowSmobList(it)  // holds the item 'status'
            viewModel.smobListProductsF = viewModel.getFlowSmobListProducts(it)

            // turn to StateFlows
            viewModel.smobListSF = viewModel.smobListFlowToStateFlow(viewModel.smobListF)
            viewModel.smobListProductsSF = viewModel.smobListProductsFlowToStateFlow(viewModel.smobListProductsF)

            // combine the flows and turn into StateFlow
            viewModel.smobListProductsWithListDataSF = viewModel.combineFlowsAndConvertToStateFlow(
                viewModel.smobListF,
                viewModel.smobListProductsF,
            )

//            // collect flows and store in StateFlow type (so that we have the latest value available
//            viewModel.fetchSmobList()
//            viewModel.fetchSmobListItems()
//
//            // combine the flows and turn into StateFlow
//            viewModel.fetchCombinedFlows()

        }

        // configure navbar
        setDisplayHomeAsUpEnabled(true)
        setTitle(String.format(getString(R.string.app_name_planning), listName))

        // install listener for SwipeRefreshLayout view
        binding.refreshLayout.setOnRefreshListener {

            // deactivate SwipeRefreshLayout spinner
            binding.refreshLayout.setRefreshing(false)

            // refresh local DB data from backend (for this list) - also updates 'showNoData'
            viewModel.swipeRefreshProductDataInLocalDB()

            // empty? --> inform user that there is no point swiping for further updates...
            if (viewModel.showNoData.value == true) {
                Toast.makeText(activity, getString(R.string.error_add_smob_items), Toast.LENGTH_SHORT).show()
            }

        }

        // refresh local DB data from backend (for this list) - also updates 'showNoData'
        viewModel.swipeRefreshProductDataInLocalDB()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // RV - incl. onClick listener for items
        setupRecyclerView()

        // "+" FAB
        binding.addSmobItemFab.setOnClickListener { navigateToPlanningProductEdit() }

        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()

        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, STARTED) to indicate when
        // the menu should be visible
        menuHost.addMenuProvider(
            object : MenuProvider {

                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.main_menu, menu)
                }

                override fun onMenuItemSelected(item: MenuItem) = when (item.itemId) {

                    // logout menu
                    R.id.logout -> {
                            // logout authenticated user
                            AuthUI.getInstance()
                                .signOut(requireContext())
                                .addOnCompleteListener {
                                    // user is now signed out -> redirect to login screen
                                    startActivity(Intent(requireContext(), SmobAuthActivity::class.java))
                                    // and we're done here
                                    requireActivity().finish()
                                }
                            true
                        }

                        // back arrow (home button)
                        android.R.id.home -> {
                            viewModel.navigationCommand.postValue(NavigationCommand.Back)
                            true
                        }

                        // unhandled...
                        else -> false

                    }  // when(item...)

            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED,
        )

    }

    // FAB handler --> navigate to PlanningProductEdit fragment
    private fun navigateToPlanningProductEdit() {
        // use the navigationCommand live data to navigate between the fragments
        viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                PlanningProductsTableFragmentDirections
                    .actionSmobPlanningProductsTableFragmentToSmobPlanningProductsAddNewItemFragment()
            )
        )
    }

    private fun setupRecyclerView() {
        val adapter = PlanningProductsTableAdapter(binding.root) {

            // this lambda is the 'callback' function which gets called when clicking an item in the
            // RecyclerView - it gets the data behind the clicked item as parameter

            // create intent which starts activity SmobDetailsActivity, with clicked data item
            val intent = SmobDetailsActivity.newIntent(
                requireContext(),
                NavigationSource.PLANNING_PRODUCT_LIST,
                it
            )

            wrapEspressoIdlingResource {
                startActivity(intent)
            }

        }  // "on-item-click" lambda

        // setup the recycler view using the extension function
        binding.smobItemsRecyclerView.setup(adapter)

        // enable swiping left/right
        val itemTouchHelper = ItemTouchHelper(PlanningProductsTableSwipeActionHandler(adapter))
        itemTouchHelper.attachToRecyclerView(binding.smobItemsRecyclerView)

    }

}