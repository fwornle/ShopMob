package com.tanfra.shopmob.smob.ui.details

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setTitle
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.databinding.FragmentDetailsShopBinding
import com.tanfra.shopmob.smob.ui.authentication.SmobAuthenticationActivity
import com.tanfra.shopmob.smob.ui.details.utils.ButtonState
import com.tanfra.shopmob.smob.ui.planning.SmobPlanningActivity
import com.tanfra.shopmob.smob.ui.shopping.SmobShoppingActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinComponent
import timber.log.Timber

class DetailsShopFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the shared ViewModel instance
    override val _viewModel: DetailsViewModel by sharedViewModel()

    // data binding for fragment_planning_lists.xml
    private lateinit var binding: FragmentDetailsShopBinding

    // lambda for contract 'StartActivityForResult', which is used to start the shopping
    // activity and return from it with a result (of what has changed)
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            result: ActivityResult ->

            if (result.resultCode == Activity.RESULT_OK) {

                // ... handle the Intent
                val intent = result.data
                Timber.i("Back from shopping... got $intent")

            }

        }  // lambda: (shopping) activity result


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // bind layout class
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_details_shop, container, false
            )

        // set injected viewModel (from KOIN service locator)
        binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        setTitle(getString(R.string.app_name_details_shop))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        // activate animation on FloorPlanButton
        binding.btFloorPlan.setState(ButtonState.Active)
        binding.btFloorPlan.setActive(true)


        // set onClick handler for FLOOR PLAN button
        // ... navigate back to the main app
        binding.btFloorPlan.setOnClickListener {

            // ... go shopping
            val intent = Intent(this.context, SmobShoppingActivity::class.java)
            startForResult.launch(intent)

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

            // note: must use 'android' to catch the back button...
            android.R.id.home -> {
                Timber.i("Back pressed from details fragment.")

                // closing this activity brings us back to where we came from (with intact
                // backstack history)
                this.activity?.finish()
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