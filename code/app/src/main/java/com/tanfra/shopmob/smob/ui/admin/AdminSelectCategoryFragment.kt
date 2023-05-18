package com.tanfra.shopmob.smob.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.FragmentAdminSelectCategoryBinding
import com.tanfra.shopmob.smob.ui.auth.SmobAuthActivity
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import com.tanfra.shopmob.smob.ui.planning.SmobPlanningActivity
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setTitle
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent

class AdminSelectCategoryFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the ViewModel instance
    override val _viewModel: AdminViewModel by activityViewModel()

    // data binding for fragment_planning_lists.xml
    private lateinit var binding: FragmentAdminSelectCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_admin_select_category, container, false
            )

        // set injected viewModel (from KOIN service locator)
        binding.viewModel = _viewModel

        setDisplayHomeAsUpEnabled(true)
        setTitle(getString(R.string.app_name_admin))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // handle PROFILE, GROUPS, LISTS button
        binding.btProfile.setOnClickListener { navigateToAdminProfileFragment() }
        binding.btGroups.setOnClickListener { navigateToAdminGroupsFragment() }
        binding.btLists.setOnClickListener { navigateToAdminListsFragment() }
        binding.btContacts.setOnClickListener { navigateToAdminContactsFragment() }

        // set onClick handler for DISMISS button
        // ... navigate back to the main app
        binding.btDismiss.setOnClickListener {
            val intent = Intent(this.context, SmobPlanningActivity::class.java)
            startActivity(intent)
            // and we're done here
            this.activity?.finish()
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


    // navigate to AdminProfile fragment
    private fun navigateToAdminProfileFragment() {
        // use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                AdminSelectCategoryFragmentDirections
                    .actionSmobAdminSelectCategoryFragmentToSmobAdminProfileFragment()
            )
        )
    }

    // navigate to AdminGroups fragment
    private fun navigateToAdminGroupsFragment() {
        // use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                AdminSelectCategoryFragmentDirections
                    .actionSmobAdminSelectCategoryFragmentToSmobAdminGroupsTableFragment()
            )
        )
    }

    // navigate to AdminLists fragment
    private fun navigateToAdminListsFragment() {
        // use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                AdminSelectCategoryFragmentDirections
                    .actionSmobAdminSelectCategoryFragmentToSmobAdminListsTableFragment()
            )
        )
    }

    // navigate to AdminContacts fragment
    private fun navigateToAdminContactsFragment() {
        // use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                AdminSelectCategoryFragmentDirections
                    .actionSmobAdminSelectCategoryFragmentToSmobAdminContactsImportFragment()
            )
        )
    }
}