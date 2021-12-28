package com.tanfra.shopmob.smob.smoblist

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
import androidx.lifecycle.viewModelScope

import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.authentication.AuthenticationActivity
import com.tanfra.shopmob.databinding.FragmentSmobItemsBinding
import com.tanfra.shopmob.smob.SmobItemDescriptionActivity
import com.tanfra.shopmob.smob.data.local.dao.SmobUserDao
import com.tanfra.shopmob.smob.data.net.api.SmobUserApi
import com.tanfra.shopmob.smob.data.repo.SmobUserRepository
import com.tanfra.shopmob.utils.setup
import com.tanfra.shopmob.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject


class SmobItemListFragment : BaseFragment() {

    // use Koin service locator to retrieve the ViewModel instance
    override val _viewModel: SmobItemListViewModel by viewModel()

    // data binding for fragment_shopmob_items.xml
    private lateinit var binding: FragmentSmobItemsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_smob_items, container, false
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

            // update smob item list
            _viewModel.loadSmobItems()

            // empty list? --> inform user that there is no point swiping for updates...
            if (_viewModel.smobItemList.value?.isEmpty() == true) {
                Toast.makeText(activity, getString(R.string.error_add_smob_items), Toast.LENGTH_SHORT).show()
            }

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setupRecyclerView()
        binding.addSmobItemFab.setOnClickListener {

            // test HTTP
            val smobUserDao: SmobUserDao by inject(SmobUserDao::class.java)
            val smobUserApi: SmobUserApi by inject(SmobUserApi::class.java)
            val userRepo = SmobUserRepository(smobUserDao, smobUserApi)
            _viewModel.viewModelScope.launch {
                userRepo.refreshSmobUserDataInDB()
            }

            // navigateToAddshopmobItem()
        }
    }

    override fun onResume() {
        super.onResume()
        //load the smob item list on the UI
        _viewModel.loadSmobItems()
    }

    // FAB handler --> navigate to SaveSmobItem fragment
    private fun navigateToAddshopmobItem() {
        // use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                SmobItemListFragmentDirections.actionShopMobItemListFragmentToSaveSmobItemFragment()
            )
        )
    }

    private fun setupRecyclerView() {
        val adapter = SmobItemListAdapter {
            // this lambda is the 'callback' function which gets called when clicking an item in the
            // RecyclerView - it gets the shopmobDataItem of the clicked item as parameter

            // create intent which starts activity shopmobItemDescriptionActivity, with extra data
            // 'shopmobItemDataItem'
            val intent = SmobItemDescriptionActivity.newIntent(requireContext(), it)
            wrapEspressoIdlingResource {
                startActivity(intent)
            }

        }

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
                        startActivity(Intent(this.context, AuthenticationActivity::class.java))
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
