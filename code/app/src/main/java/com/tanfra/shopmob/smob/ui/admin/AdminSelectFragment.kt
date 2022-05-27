package com.tanfra.shopmob.smob.ui.admin

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setTitle
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.content.Intent

import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.databinding.FragmentAdminSelectBinding
import com.tanfra.shopmob.smob.ui.auth.SmobAuthActivity
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import com.tanfra.shopmob.smob.ui.planning.SmobPlanningActivity
import org.koin.core.component.KoinComponent

class AdminSelectFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the ViewModel instance
    override val _viewModel: AdminViewModel by viewModel()

    // data binding for fragment_planning_lists.xml
    private lateinit var binding: FragmentAdminSelectBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_admin_select, container, false
            )

        // set injected viewModel (from KOIN service locator)
        binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
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

        // set onClick handler for DISMISS button
        // ... navigate back to the main app
        binding.btDismiss.setOnClickListener {
            val intent = Intent(this.context, SmobPlanningActivity::class.java)
            startActivity(intent)
            // and we're done here
            this.activity?.finish()
        }
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

    // navigate to AdminProfile fragment
    private fun navigateToAdminProfileFragment() {
        // use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                AdminSelectFragmentDirections.actionSmobAdminSelectFragmentToSmobAdminProfileFragment()
            )
        )
    }

    // navigate to AdminGroups fragment
    private fun navigateToAdminGroupsFragment() {
        // use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                AdminSelectFragmentDirections.actionSmobAdminSelectFragmentToSmobAdminGroupsFragment()
            )
        )
    }

    // navigate to AdminLists fragment
    private fun navigateToAdminListsFragment() {
        // use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                AdminSelectFragmentDirections.actionSmobAdminSelectFragmentToSmobAdminListsFragment()
            )
        )
    }

}