package com.tanfra.shopmob.smob.ui.planning.lists

import android.os.Bundle
import android.view.*
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseFragment
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationCommand
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setTitle
import android.content.Intent
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.smob.ui.auth.SmobAuthActivity
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import com.tanfra.shopmob.smob.ui.planning.lists.components.PlanningListsScreen
import com.tanfra.shopmob.smob.ui.shopping.SmobShoppingActivity
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent

class PlanningListsTableFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the ViewModel instance(s)
    override val viewModel: PlanningViewModel by activityViewModel()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // TODO - remove --> to be done in Scaffold
        setDisplayHomeAsUpEnabled(true)
        setTitle(getString(R.string.app_name))

        // collect SmobLists from local DB
        viewModel.loadLists()

        // construct view (compose)
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PlanningListsScreen(viewModel)
            }
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        // RV - incl. onClick listener for items
//        setupRecyclerView()
//
//        // handlers for "+" FAB, "SHOP" FAB and "STORE" FAB
//        binding.addSmobItemFab.setOnClickListener { navigateToAddSmobList() }
//        binding.goShop.setOnClickListener { navigateToShopping() }
//        binding.defineShop.setOnClickListener { navigateToShopEditFragment() }

        // TODO - removed this --> to be done in Scaffold

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
        viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                PlanningListsTableFragmentDirections.actionSmobPlanningListsTableFragmentToSmobPlanningShopsAddNewItemFragment()
            )
        )
    }

}