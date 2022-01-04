package com.tanfra.shopmob.smob.activities.planning.lists

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.base.BaseFragment
import com.tanfra.shopmob.base.NavigationCommand
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setTitle
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.content.Intent
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.databinding.FragmentPlanningListsBinding
import com.tanfra.shopmob.smob.activities.administration.SmobAdminTask
import com.tanfra.shopmob.smob.activities.administration.SmobAdministrationActivity
import com.tanfra.shopmob.smob.activities.authentication.SmobAuthenticationActivity
import com.tanfra.shopmob.utils.setup
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import org.koin.core.component.KoinComponent

class PlanningListsFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the ViewModel instance
    override val _viewModel: PlanningListsViewModel by viewModel()

    // data binding for fragment_planning_lists.xml
    private lateinit var binding: FragmentPlanningListsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_planning_lists, container, false
            )

        // set injected viewModel (from KOIN service locator)
        binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))

        // install listener for SwipeRefreshLayout view
        binding.refreshLayout.setOnRefreshListener {

            // deactivate SwipeRefreshLayout spinner
            binding.refreshLayout.setRefreshing(false)

            // update smob list
            // ... this also updates LifeData 'showNoData' (see below)
            _viewModel.loadListItems()

            // empty list? --> inform user that there is no point swiping for updates...
            if (_viewModel.showNoData.value == true) {
                Toast.makeText(activity, getString(R.string.error_add_smob_items), Toast.LENGTH_SHORT).show()
            }

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        setupRecyclerView()
        binding.addSmobItemFab.setOnClickListener {
            navigateToAddSmobList()
        }
    }

    override fun onResume() {
        super.onResume()
        //load the smob item list on the UI
        _viewModel.loadListItems()
    }

    // FAB handler --> navigate to selected fragment of the admin activity
    private fun navigateToAddSmobList() {
        // request fragment "list administration"
        val intent = SmobAdministrationActivity.newIntent(requireContext(), SmobAdminTask.NEW_LIST)
        wrapEspressoIdlingResource {
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        val adapter = PlanningListsAdapter {

            // this lambda is the 'callback' function which gets called when clicking an item in the
            // RecyclerView - it gets the data behind the clicked item as parameter

            // use the navigationCommand live data to navigate between the fragments
            _viewModel.navigationCommand.postValue(
                NavigationCommand.To(
                    PlanningListsFragmentDirections.actionPlanningListsFragmentToPlanningProductListFragment()
                )
            )

        }  // "on-item-click" lambda

        // setup the recycler view using the extension function
        binding.smobItemsRecyclerView.setup(adapter)
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