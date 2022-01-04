package com.tanfra.shopmob.smob.activities.planning.shopList

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.base.BaseFragment
import com.tanfra.shopmob.base.NavigationCommand
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setTitle
import android.content.Intent
import android.widget.Toast

import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.databinding.FragmentPlanningShopListBinding
import com.tanfra.shopmob.smob.activities.authentication.SmobAuthenticationActivity
import com.tanfra.shopmob.smob.activities.details.SmobDetailsActivity
import com.tanfra.shopmob.smob.activities.details.SmobDetailsSources
import com.tanfra.shopmob.utils.setup
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent


class PlanningShopListFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the (shared) ViewModel instance
    override val _viewModel: PlanningShopListViewModel by viewModel()

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
        setDisplayHomeAsUpEnabled(false)
        setTitle(String.format(getString(R.string.app_name_planning), ": Shops"))

        // install listener for SwipeRefreshLayout view
        binding.rlPlanningShopList.setOnRefreshListener {

            // deactivate SwipeRefreshLayout spinner
            binding.rlPlanningShopList.setRefreshing(false)

            // update smob list
            // ... this also updates LifeData 'showNoData' (see below)
            _viewModel.loadShopItems()

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
            navigateToAddshopmobItem()
        }
    }

    override fun onResume() {
        super.onResume()
        //load the smob item list on the UI
        _viewModel.loadShopItems()
    }

    // FAB handler --> navigate to SaveSmobItem fragment
    private fun navigateToAddshopmobItem() {
        // use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                PlanningShopListFragmentDirections.actionPlanningShopListFragmentToPlanningProductEditFragment()
            )
        )
    }

    private fun setupRecyclerView() {
        val adapter = PlanningShopListAdapter {

            // this lambda is the 'callback' function which gets called when clicking an item in the
            // RecyclerView - it gets the data behind the clicked item as parameter

            // create intent which starts activity SmobDetailsActivity, with clicked data item
            val intent = SmobDetailsActivity.newIntent(requireContext(), SmobDetailsSources.PLANNING_SHOP_LIST, it)
            wrapEspressoIdlingResource {
                startActivity(intent)
            }

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