package com.tanfra.shopmob.smob.ui.admin.lists

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setTitle
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.content.Intent
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.databinding.FragmentAdminListsBinding
import com.tanfra.shopmob.smob.data.local.utils.ShopCategory
import com.tanfra.shopmob.smob.ui.admin.AdminViewModel
import com.tanfra.shopmob.smob.ui.auth.SmobAuthActivity
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import com.tanfra.shopmob.smob.ui.planning.SmobPlanningActivity
import org.koin.core.component.KoinComponent
import timber.log.Timber

class AdminListsFragment : BaseFragment(), AdapterView.OnItemSelectedListener, KoinComponent {

    // use Koin service locator to retrieve the ViewModel instance
    override val _viewModel: AdminViewModel by viewModel()

    // data binding for fragment_planning_lists.xml
    private lateinit var binding: FragmentAdminListsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_admin_lists, container, false
            )

        // set injected viewModel (from KOIN service locator)
        binding.viewModel = _viewModel

        setHasOptionsMenu(false)
        setDisplayHomeAsUpEnabled(true)
        setTitle(getString(R.string.app_name_admin_lists))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // configure spinner (shop category)
        setupSpinners()

        // set onClick handler for DISMISS button
        // ... navigate back to the main app
        binding.btDismiss.setOnClickListener {
            val intent = Intent(this.context, SmobPlanningActivity::class.java)
            startActivity(intent)
            // and we're done here
            this.activity?.finish()
        }
    }

    // set-up spinners (categories)
    private fun setupSpinners() {

        // populate 'smobList' (liveData)
        _viewModel.loadListItems()

        val myList = _viewModel.smobList.value
        Timber.i(myList.toString())
        // TODO
        // TODO
        // TODO
        // TODO


        // ShopCategory
        // create an ArrayAdapter using the enum and a default spinner layout
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ShopCategory.values())
            .also {
                // specify the layout to use when the list of choices appears
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // apply the adapter to the spinner
                binding.smobShopLists.adapter = it
            }

        // hook up onItemSelected listener (to this fragment)
        binding.smobShopLists.onItemSelectedListener = this

    }

    // spinner: item selection made
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        // we might have several spinners at some point...
        when(p0) {
            binding.smobShopLists -> {
                // set shop category in VM
                //_viewModel.locatedShop.value?.category = ShopCategory.values()[p2]
            }
            else -> {
                // should not happen - unless someone added more spinners
                Timber.i("Unknown spinner found: $p0")
            }
        }
    }

    // spinner: no item selected
    override fun onNothingSelected(p0: AdapterView<*>?) {
        Timber.i("No selection made... (this is never called?!) $p0")
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

}