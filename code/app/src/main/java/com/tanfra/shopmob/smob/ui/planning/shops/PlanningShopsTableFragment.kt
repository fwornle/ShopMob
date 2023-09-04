package com.tanfra.shopmob.smob.ui.planning.shops

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkRequest
import androidx.recyclerview.widget.ItemTouchHelper
import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.FragmentPlanningShopsTableBinding
import com.tanfra.shopmob.smob.ui.auth.SmobAuthActivity
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseFragment
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationCommand
import com.tanfra.shopmob.smob.ui.details.SmobDetailsActivity
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationSource
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setTitle
import com.tanfra.shopmob.utils.setup
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent
import timber.log.Timber


class PlanningShopsTableFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the (shared) ViewModel instance
    override val viewModel: PlanningViewModel by activityViewModel()

    // data binding for fragment_planning_shop_list.xml
    private lateinit var binding: FragmentPlanningShopsTableBinding

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_planning_shops_table, container, false
            )

        // set injected viewModel (from KOIN service locator)
        binding.viewModel = viewModel

        setDisplayHomeAsUpEnabled(true)
        setTitle(String.format(getString(R.string.app_name_planning_shop), "Shops"))

        // install listener for SwipeRefreshLayout view
        binding.rlPlanningShopList.setOnRefreshListener {

            // deactivate SwipeRefreshLayout spinner
            binding.rlPlanningShopList.setRefreshing(false)

            // refresh local DB data from backend (for this list) - also updates 'showNoData'
            viewModel.swipeRefreshShopDataInLocalDB()

            // empty? --> inform user that there is no point swiping for further updates...
            if (viewModel.showNoData.value == true) {
                Toast.makeText(activity, getString(R.string.error_add_smob_items), Toast.LENGTH_SHORT).show()
            }

        }

        // refresh local DB data from backend (for this list) - also updates 'showNoData'
        viewModel.swipeRefreshShopDataInLocalDB()

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

    // FAB handler --> navigate to SaveSmobItem fragment
    private fun navigateToAddshopmobItem() {
        // use deepLink to reach this fragment
        val request = NavDeepLinkRequest.Builder
            .fromUri("android-app://com.tanfra.shopmob/planningShopsAddNew".toUri())
            .build()

        viewModel.navigationCommand.postValue(
            NavigationCommand.ToDeepLink(request)
        )
    }

    private fun setupRecyclerView() {
        val adapter = PlanningShopsTableAdapter(binding.root) {

            // this lambda is the 'callback' function which gets called when clicking an item in the
            // RecyclerView - it gets the data behind the clicked item as parameter

            // click listener with "modal" reaction
            when(viewModel.navSource) {

                // entered here from the "navDrawer"
                "navDrawer" -> {

                    // clicks should take us through to the SmobShop details screen
                    val context = requireContext()
                    val intent = SmobDetailsActivity.newIntent(
                        context,
                        NavigationSource.PLANNING_SHOP_LIST,
                        it
                    )
                    ContextCompat.startActivity(context, intent, null)

                }  // navDrawer

                // entered here from "edit product" screen
                else -> {

                    // reset navigation source to default
                    viewModel.navSource = "navDrawer"

                    // clicks should select the shop and return to the product edit screen
                    val daFlow = viewModel.shopDataSource.getSmobItem(it.id)
                    daFlow
                        .take(1)
                        .onEach {
                            Timber.i("Received shop: ${it.data?.name}")
                        }
                        .launchIn(viewLifecycleOwner.lifecycleScope)  // co-routine scope

                    // navigate back to smobPlanningProductEditFragment
                    // ... communicate the selected SmobShop via shared ViewModel
                    viewModel.selectedShop.postValue(it)

                    // use the navigationCommand live data to navigate between the fragments
                    viewModel.navigationCommand.postValue(NavigationCommand.Back)

                }  // product definition --> shop selection

            }  // when (modal response of list item click listener)

        }  // "on-item-click" lambda

        // setup the recycler view using the extension function
        binding.smobItemsRecyclerView.setup(adapter)

        // enable swiping left/right
        val itemTouchHelper = ItemTouchHelper(PlanningShopsTableSwipeActionHandler(adapter))
        itemTouchHelper.attachToRecyclerView(binding.smobItemsRecyclerView)

    }

}