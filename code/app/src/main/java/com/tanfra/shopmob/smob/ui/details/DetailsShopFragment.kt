package com.tanfra.shopmob.smob.ui.details

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.FragmentDetailsShopBinding
import com.tanfra.shopmob.smob.ui.auth.SmobAuthActivity
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.smob.ui.details.utils.ButtonState
import com.tanfra.shopmob.smob.ui.planning.SmobPlanningActivity
import com.tanfra.shopmob.smob.ui.shopping.SmobShoppingActivity
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setTitle
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
        setTitle(getString(R.string.app_name_details_shop))

        // no up button when navigating to here from "void" (Android -> GeoFence)
        when(_viewModel.navSource) {
            SmobDetailsSources.GEOFENCE -> setDisplayHomeAsUpEnabled(false)
            else -> setDisplayHomeAsUpEnabled(true)
        }

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

        // set onClick handler for location link
        // ... navigate to the map
        binding.tvLocText.setOnClickListener {

            // create a Uri from an intent string. Use the result to create an Intent.
            val gmmIntentUri = Uri.parse("google.streetview:cbll=" +
                    "${_viewModel.smobShopDetailsItem.value?.location?.latitude}," +
                    "${_viewModel.smobShopDetailsItem.value?.location?.longitude}")

            // create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

            // make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage("com.google.android.apps.maps")

            // attempt to start an activity that can handle the Intent
            startActivity(mapIntent)

//            // open map using a "deep link"
//            // ... see: https://stackoverflow.com/questions/59985632/navigate-to-a-fragment-from-another-graph-without-it-being-the-start-destination
//            val uri = Uri.parse(getString(R.string.deepLinkMap))
//            findNavController().navigate(
//                uri,
//                navOptions { // Use the Kotlin DSL for building NavOptions
//                    anim {
//                        enter = android.R.animator.fade_in
//                        exit = android.R.animator.fade_out
//                    }
//                }
//            )

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // display menu item depending on where we came from...
        when(_viewModel.navSource) {

            // entered from a geofence trigger (special)
            SmobDetailsSources.GEOFENCE -> {

                when (item.itemId) {

                    R.id.smobPlanningListsTableFragment -> {
                        // start the app
                        startActivity(Intent(this.context, SmobPlanningActivity::class.java))
                        // and we're done here
                        this.activity?.finish()
                    }

                }  // when(item...)

            }

            // entered from within the app (normal)
            else -> {

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

                    // note: must use 'android' to catch the back button...
                    android.R.id.home -> {
                        Timber.i("Back pressed from details fragment.")

                        // closing this activity brings us back to where we came from (with intact
                        // backstack history)
                        this.activity?.finish()
                    }

                }  // when(item...)

            }

        }



        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        // display menu item depending on where we came from...
        when(_viewModel.navSource) {
            SmobDetailsSources.GEOFENCE -> inflater.inflate(R.menu.geofence_shop_menu, menu)
            else -> inflater.inflate(R.menu.main_menu, menu)
        }

    }

}