package com.tanfra.shopmob.smob.ui.admin.contacts

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setTitle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import com.tanfra.shopmob.BuildConfig
import org.koin.core.component.KoinComponent
import com.tanfra.shopmob.databinding.FragmentAdminContactsImportTableBinding
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import com.tanfra.shopmob.utils.hasPermission
import com.tanfra.shopmob.utils.setup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class AdminContactsImportTableFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the ViewModel instance
    override val _viewModel: AdminViewModel by sharedViewModel()

    // data binding for fragment_admin_contacts_import_table.xml
    private lateinit var binding: FragmentAdminContactsImportTableBinding

    // permissions (access to contact details)
    private lateinit var contactsAccessPermissionRequest: ActivityResultLauncher<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_admin_contacts_import_table, container, false
            )

        // set injected viewModel (from KOIN service locator)
        binding.viewModel = _viewModel

        // configure navbar
        setDisplayHomeAsUpEnabled(true)
        setTitle(getString(R.string.import_contacts))

        // install listener for SwipeRefreshLayout view
        binding.refreshLayout.setOnRefreshListener {

            // deactivate SwipeRefreshLayout spinner
            binding.refreshLayout.setRefreshing(false)


            // fetch contacts data (after asking for permission)
            if (requireContext().hasPermission(Manifest.permission.READ_CONTACTS)) {
                // permissions already granted --> fetch contacts data
                _viewModel.fetchContacts()
            } else {
                // initiate permission check for (read) access to contacts on this device
                contactsAccessPermissionRequest.launch(Manifest.permission.READ_CONTACTS)
            }

            // empty? --> inform user that there is no point swiping for further updates...
            if (_viewModel.showNoData.value == true) {
                Toast.makeText(activity, getString(R.string.error_add_smob_users), Toast.LENGTH_SHORT).show()
            }

        }

        // handling of access to contacts stored on this devices
        registerAccessContactsPermissionCheck()

        val adapter = AdminContactsImportTableAdapter(binding.root) {

            // this lambda is the 'callback' function which gets called when clicking an item in the
            // RecyclerView - it gets the data behind the clicked item as parameter

            // communicate the selected item (= a contact)
            _viewModel.currSmobContactATO = it

            // use the navigationCommand live data to navigate between the fragments
            _viewModel.navigationCommand.postValue(
                NavigationCommand.To(
                    AdminContactsImportTableFragmentDirections
                        .actionSmobAdminContactsImportTableFragmentToSmobAdminSelectCategoryFragment()
                )
            )

        }  // "on-item-click" lambda

        // connect SearchView to RecyclerView
        with(binding) {

            // connect SearchView to RecyclerView
            smobContactsImportSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return false
                }
            })

            // setup the recycler view using the extension function
            smobItemsRecyclerView.setup(adapter)

        }

        // enable swiping left/right (comment out to disable swiping)
        val itemTouchHelper = ItemTouchHelper(AdminContactsImportTableSwipeActionHandler(adapter))
        itemTouchHelper.attachToRecyclerView(binding.smobItemsRecyclerView)

        // fetch contacts data (after asking for permission)
        if (requireContext().hasPermission(Manifest.permission.READ_CONTACTS)) {
            // permissions already granted --> fetch contacts data
            _viewModel.fetchContacts()
        } else {
            // initiate permission check for (read) access to contacts on this device
            contactsAccessPermissionRequest.launch(Manifest.permission.READ_CONTACTS)
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // "+" FAB
        binding.importContactsFab.setOnClickListener { navigateToAdminSelectCategory() }

    }

    // FAB handler --> navigate back to AdminSelectCategory fragment
    private fun navigateToAdminSelectCategory() {

        // use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(NavigationCommand.Back)

    }

    // request access to contact details stored on this device
    private fun registerAccessContactsPermissionCheck() {

        // register handler (lambda) for permission launcher
        contactsAccessPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (isGranted) {
                // access to contact details (stored on this device) has been granted
                _viewModel.fetchContacts()
            } else {
                // access has NOT been granted
                // --> inform user and send them to settings
                Snackbar.make(
                    binding.clContactsImportTable,
                    R.string.access_contacts_permission_denied_consequence,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.settings) {
                    startActivity(
                        Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    )
                }.show()
            }

        }  // activityResult (lambda)

    }  // registerAccessContactsPermissionCheck

}