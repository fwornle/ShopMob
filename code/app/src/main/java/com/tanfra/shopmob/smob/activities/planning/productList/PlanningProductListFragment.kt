package com.tanfra.shopmob.smob.activities.planning.productList

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
import com.tanfra.shopmob.databinding.FragmentPlanningProductListBinding
import com.tanfra.shopmob.smob.activities.authentication.SmobAuthenticationActivity
import com.tanfra.shopmob.smob.activities.details.SmobDetailsActivity
import com.tanfra.shopmob.smob.activities.details.SmobDetailsSources
import com.tanfra.shopmob.utils.setup
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import org.koin.androidx.viewmodel.ext.android.viewModel

import org.koin.core.component.KoinComponent
import timber.log.Timber


class PlanningProductListFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the ViewModel instance
    override val _viewModel: PlanningProductListViewModel by viewModel()

    // data binding for fragment_smob_planning_lists.xml
    private lateinit var binding: FragmentPlanningProductListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_planning_product_list, container, false
            )

        // set injected viewModel (from KOIN service locator)
        binding.viewModel = _viewModel

        // fetch ID of list to be displayed (from incoming bundle)
        val listId = arguments?.getString("listId")
        Timber.i("Showing products on list with ID: $listId")

        val listName = arguments?.getString("listName")
        Timber.i("... list name: $listName")

        // register flows fetch items of the selected upstream list
        listId?.let {
            _viewModel.smobList = _viewModel.fetchListItems(listId)
        }

        // configure navbar
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        setTitle(String.format(getString(R.string.app_name_planning), listName))

        // install listener for SwipeRefreshLayout view
        binding.refreshLayout.setOnRefreshListener {

            // deactivate SwipeRefreshLayout spinner
            binding.refreshLayout.setRefreshing(false)

            // refresh local DB data from backend
            _viewModel.swipeRefreshDataInLocalDB()

            // empty? --> inform user that there is no point swiping for further updates...
            if (_viewModel.showNoData.value == true) {
                Toast.makeText(activity, getString(R.string.error_add_smob_items), Toast.LENGTH_SHORT).show()
            }

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // RV - incl. onClick listener for items
        setupRecyclerView()

        // "+" FAB
        binding.addSmobItemFab.setOnClickListener {
            navigateToPlanningProductEdit()
        }
    }

    // FAB handler --> navigate to PlanningProductEdit fragment
    private fun navigateToPlanningProductEdit() {
        // use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                PlanningProductListFragmentDirections.actionPlanningProductListFragmentToPlanningProductEditFragment()
            )
        )
    }

    private fun setupRecyclerView() {
        val adapter = PlanningProductListAdapter {

            // this lambda is the 'callback' function which gets called when clicking an item in the
            // RecyclerView - it gets the data behind the clicked item as parameter

            // create intent which starts activity SmobDetailsActivity, with clicked data item
            val intent = SmobDetailsActivity.newIntent(
                requireContext(),
                SmobDetailsSources.PLANNING_PRODUCT_LIST,
                it
            )

            wrapEspressoIdlingResource {
                startActivity(intent)
            }

//            // alternatively: include target activity in nav_graph_planning (= current graph)
//            // ... and simply use this as new destination...
//            val bundle = Bundle().apply {
//                putSerializable("EXTRA_Source", SmobDetailsSources.PLANNING_PRODUCT_LIST)
//                putSerializable("EXTRA_SmobItem", it)
//            }
//            findNavController().navigate(R.id.smobDetailsActivity, bundle)

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