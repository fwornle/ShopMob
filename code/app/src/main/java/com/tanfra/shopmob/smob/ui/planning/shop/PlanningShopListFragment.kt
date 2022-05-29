package com.tanfra.shopmob.smob.ui.planning.shop

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
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.databinding.FragmentPlanningShopListBinding
import com.tanfra.shopmob.smob.ui.auth.SmobAuthActivity
import com.tanfra.shopmob.smob.ui.details.SmobDetailsActivity
import com.tanfra.shopmob.smob.ui.details.SmobDetailsSources
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import com.tanfra.shopmob.utils.setup
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinComponent
import timber.log.Timber


class PlanningShopListFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the (shared) ViewModel instance
    override val _viewModel: PlanningViewModel by sharedViewModel()

    // data binding for fragment_planning_shop_list.xml
    private lateinit var binding: FragmentPlanningShopListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_planning_shop_list, container, false
            )

        // set injected viewModel (from KOIN service locator)
        binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        setTitle(String.format(getString(R.string.app_name_planning_shop), "Shops"))

        // install listener for SwipeRefreshLayout view
        binding.rlPlanningShopList.setOnRefreshListener {

            // deactivate SwipeRefreshLayout spinner
            binding.rlPlanningShopList.setRefreshing(false)

            // refresh local DB data from backend (for this list) - also updates 'showNoData'
            _viewModel.swipeRefreshShopDataInLocalDB()

            // empty? --> inform user that there is no point swiping for further updates...
            if (_viewModel.showNoData.value == true) {
                Toast.makeText(activity, getString(R.string.error_add_smob_items), Toast.LENGTH_SHORT).show()
            }

        }

        // refresh local DB data from backend (for this list) - also updates 'showNoData'
        _viewModel.swipeRefreshShopDataInLocalDB()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // RV - incl. onClick listener for items
        setupRecyclerView()

        // "+" FAB
        binding.addSmobItemFab.setOnClickListener {
            navigateToAddshopmobItem()
        }
    }

    // FAB handler --> navigate to SaveSmobItem fragment
    private fun navigateToAddshopmobItem() {
        // use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                PlanningShopListFragmentDirections.actionSmobPlanningShopListFragmentToSmobPlanningShopEditFragment()
            )
        )
    }

    private fun setupRecyclerView() {
        val adapter = PlanningShopListAdapter(binding.root) {

            // this lambda is the 'callback' function which gets called when clicking an item in the
            // RecyclerView - it gets the data behind the clicked item as parameter

            // click listener with "modal" reaction
            when(_viewModel.navSource) {

                // entered here from the "navDrawer"
                "navDrawer" -> {

                    // clicks should take us through to the SmobShop details screen
                    val context = requireContext()
                    val intent = SmobDetailsActivity.newIntent(
                        context,
                        SmobDetailsSources.PLANNING_SHOP_LIST,
                        it
                    )
                    ContextCompat.startActivity(context, intent, null)

                }  // navDrawer

                // entered here from "edit product" screen
                else -> {

                    // reset navigation source to default
                    _viewModel.navSource = "navDrawer"

                    // clicks should select the shop and return to the product edit screen
                    val daFlow = _viewModel.shopDataSource.getSmobItem(it.id)
                    viewLifecycleOwner.lifecycleScope.launch {
                        daFlow.take(1).collect {
                            Timber.i("Received shop: ${it.data?.name}")
                        }
                    }

                    // navigate back to smobPlanningProductEditFragment
                    // ... communicate the selected SmobShop via shared ViewModel
                    _viewModel.selectedShop.postValue(it)

                    // use the navigationCommand live data to navigate between the fragments
                    _viewModel.navigationCommand.postValue(NavigationCommand.Back)

                }  // product definition --> shop selection

            }  // when (modal response of list item click listener)

        }  // "on-item-click" lambda

        // setup the recycler view using the extension function
        binding.smobItemsRecyclerView.setup(adapter)

        // enable swiping left/right
        val itemTouchHelper = ItemTouchHelper(PlanningShopListSwipeActionHandler(adapter))
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