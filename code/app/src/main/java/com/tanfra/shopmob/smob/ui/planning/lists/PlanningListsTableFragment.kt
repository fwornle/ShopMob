package com.tanfra.shopmob.smob.ui.planning.lists

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
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.databinding.FragmentPlanningListsTableBinding
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.ui.auth.SmobAuthActivity
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import com.tanfra.shopmob.smob.ui.shopping.SmobShoppingActivity
import com.tanfra.shopmob.utils.setup
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent


class PlanningListsTableFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the ViewModel instance(s)
    override val _viewModel: PlanningViewModel by activityViewModel()

    // data binding for fragment_planning_lists.xml
    private lateinit var binding: FragmentPlanningListsTableBinding


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_planning_lists_table, container, false
            )

        // set injected viewModel (from KOIN service locator)
        binding.viewModel = _viewModel

        setDisplayHomeAsUpEnabled(true)
        setTitle(getString(R.string.app_name))

        // install listener for SwipeRefreshLayout view
        binding.refreshLayout.setOnRefreshListener {

            // deactivate SwipeRefreshLayout spinner
            binding.refreshLayout.setRefreshing(false)

            // refresh local DB data from backend (for this list) - also updates 'showNoData'
            _viewModel.swipeRefreshDataInLocalDB()

            // empty? --> inform user that there is no point swiping for further updates...
            if (_viewModel.showNoData.value == true) {
                Toast.makeText(activity, getString(R.string.error_add_smob_lists), Toast.LENGTH_SHORT).show()
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

        // handlers for "+" FAB, "SHOP" FAB and "STORE" FAB
        binding.addSmobItemFab.setOnClickListener { navigateToAddSmobList() }
        binding.goShop.setOnClickListener { navigateToShopping() }
        binding.defineShop.setOnClickListener { navigateToShopEditFragment() }

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
                        _viewModel.navigationCommand.postValue(NavigationCommand.Back)
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

    // "+" FAB handler --> navigate to selected fragment of the admin activity
    private fun navigateToAddSmobList() {

        // determine hightest index in all smobLists
        val highPos = _viewModel.smobLists.value.let {
            if (it.status == Status.SUCCESS) {
                // return  highest index
                it.data?.fold(0L) { max, list -> if (list?.itemPosition!! > max) list.itemPosition else max } ?: 0L
            } else {
                0L
            }
        }

        // communicate the currently highest list position
        val bundle = bundleOf(
            "listPosMax" to highPos,
        )

        // use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.ToWithBundle(
                R.id.smobPlanningListsAddNewItemFragment,
                bundle
            )
        )

    }

    // "SHOP" FAB handler --> navigate to shopping activity (SmobShoppingActivity)
    private fun navigateToShopping() {
        val intent = SmobShoppingActivity.newIntent(requireContext())
        wrapEspressoIdlingResource {
            startActivity(intent)
        }
    }

    // "STORE" FAB handler --> navigate to shop/store management fragment
    private fun navigateToShopEditFragment() {
        // use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                PlanningListsTableFragmentDirections.actionSmobPlanningListsTableFragmentToSmobPlanningShopsAddNewItemFragment()
            )
        )
    }

    private fun setupRecyclerView() {
        val adapter = PlanningListsTableAdapter(binding.root) {

            // this lambda is the 'callback' function which gets called when clicking an item in the
            // RecyclerView - it gets the data behind the clicked item as parameter

            // communicate the ID and name of the selected item (= shopping list)
            val bundle = bundleOf(
                "listId" to it.itemId.value,
                "listName" to it.name,
            )

            // use the navigationCommand live data to navigate between the fragments
            _viewModel.navigationCommand.postValue(
                NavigationCommand.ToWithBundle(
                    R.id.smobPlanningProductsTableFragment,
                    bundle
                )
            )

        }  // "on-item-click" lambda

        // setup the recycler view using the extension function
        binding.smobItemsRecyclerView.setup(adapter)

        // enable swiping left/right
        val itemTouchHelper = ItemTouchHelper(PlanningListsTableSwipeActionHandler(adapter))
        itemTouchHelper.attachToRecyclerView(binding.smobItemsRecyclerView)

    }

}