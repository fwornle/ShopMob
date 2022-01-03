package com.tanfra.shopmob.smob.activities.details

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.base.BaseFragment
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setTitle
import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.databinding.FragmentDetailsProductBinding
import com.tanfra.shopmob.smob.activities.authentication.SmobAuthenticationActivity
import com.tanfra.shopmob.smob.activities.planning.SmobPlanningActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinComponent

class DetailsProductFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the shared ViewModel instance
    override val _viewModel: DetailsViewModel by sharedViewModel()

    // data binding for fragment_planning_lists.xml
    private lateinit var binding: FragmentDetailsProductBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_details_product, container, false
            )

        // set injected viewModel (from KOIN service locator)
        binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name_details_product))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // set onClick handler for DISMISS button
        // ... navigate back to the main app
        binding.btDismiss.setOnClickListener {
            val intent = Intent(this.context, SmobPlanningActivity::class.java).apply {
                this.putExtra("smobActivityReturn", "currProductList")
            }
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