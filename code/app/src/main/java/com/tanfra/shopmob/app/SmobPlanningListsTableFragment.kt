package com.tanfra.shopmob.app

import android.os.Bundle
import android.view.*
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseFragment
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationCommand
import android.content.Intent
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.features.commonScaffold.presentation.view.ScaffoldScreen
import com.tanfra.shopmob.smob.ui.auth.SmobAuthActivity
import com.tanfra.shopmob.features.smobPlanning.presentation.obsoleteRemove.PlanningViewModel
import com.tanfra.shopmob.features.smobPlanning.router.PlanningRoutes
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent

class SmobPlanningListsTableFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the ViewModel instance(s)
    override val viewModel: PlanningViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            ScaffoldScreen(
                viewModel = koinViewModel(),
                startTitle = PlanningRoutes.PlanningScreens.title,
                startDestination = PlanningRoutes.PlanningScreens.route,
                getBottomBarDestItems = PlanningRoutes.PlanningScreens.getBottomBarDestinations,
                getDrawerMenuDestItems = PlanningRoutes.PlanningScreens.getDrawerMenuDestinations,
            )
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

}