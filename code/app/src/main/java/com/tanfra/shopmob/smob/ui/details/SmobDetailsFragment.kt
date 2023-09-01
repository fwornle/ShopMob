package com.tanfra.shopmob.smob.ui.details

import android.app.Activity
import android.os.Bundle
import android.view.*
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.utils.setTitle
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.firebase.ui.auth.AuthUI
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.ui.auth.SmobAuthActivity
import com.tanfra.shopmob.smob.ui.details.components.SmobItemDetailsScreen
import com.tanfra.shopmob.smob.ui.shopping.SmobShoppingActivity
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent
import timber.log.Timber

class SmobDetailsFragment : BaseFragment(), KoinComponent {

    // use Koin service locator to retrieve the shared ViewModel instance
    override val viewModel: SmobDetailsViewModel by activityViewModel()

    // lambda for contract 'StartActivityForResult', which is used to start the shopping
    // activity and return from it with a result (of what has changed)
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            // ... handle the Intent
            val intent = result.data
            Timber.i("Back from shopping... got $intent")
        }
    }  // lambda: (shopping) activity result

    // callback, allowing the user to be sent to Google Maps
    private val sendToMap = {

        // concrete type
        val daShop = viewModel.viewState.value.item as SmobShopATO

        // create a Uri from an intent string. Use the result to create an Intent.
        val gmmIntentUri = Uri.parse("google.streetview:cbll=" +
                "${daShop.location.latitude}," +
                "${daShop.location.longitude}")

        // create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

        // make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps")

        // attempt to start an activity that can handle the Intent
        startActivity(mapIntent)

    }  // sendToMap


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // set (fragment based) callback functions needed by view SmobDetailsShop
        viewModel.sendToMap = sendToMap
        viewModel.sendToShop = {
            startForResult.launch(Intent(this.context, SmobShoppingActivity::class.java))
        }


        // retrieve navSource (from viewState)
        val navSource = viewModel.viewState.value.navSource

        // set title
        when(navSource) {

            // shop details
            SmobDetailsNavSources.PLANNING_SHOP_LIST,
            SmobDetailsNavSources.GEOFENCE -> {
                setTitle(getString(R.string.app_name_details_shop))
            }

            // product details
            SmobDetailsNavSources.PLANNING_PRODUCT_LIST -> {
                setTitle(getString(R.string.app_name_details_product))
            }

            // unknown
            else -> {
                setTitle(getString(R.string.app_name))
            }

        }

        // no up button when navigating to here from outside the app (eg. Android -> GeoFence)
        when(navSource) {
            SmobDetailsNavSources.GEOFENCE -> setDisplayHomeAsUpEnabled(false)
            else -> setDisplayHomeAsUpEnabled(true)
        }


        // construct view (compose)
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SmobItemDetailsScreen(viewModel)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()

        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, STARTED) to indicate when
        // the menu should be visible
        menuHost.addMenuProvider(
            object : MenuProvider {

                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.main_menu, menu)
                }

                override fun onMenuItemSelected(item: MenuItem) = when (item.itemId) {

                    // logout menu
                    R.id.logout -> {
                        // logout authenticated user
                        AuthUI.getInstance()
                            .signOut(requireContext())
                            .addOnCompleteListener {
                                // user is now signed out -> redirect to login screen
                                startActivity(Intent(requireContext(), SmobAuthActivity::class.java))
                                // and we're done here
                                requireActivity().finish()
                            }
                        true
                    }

                    // back arrow (home button)
                    android.R.id.home -> {

                        Timber.i("Back pressed from details fragment.")

                        // closing this activity brings us back to where we came from (with intact
                        // backstack history
                        requireActivity().finish()
                        true
                    }

                    // unhandled...
                    else -> false

                }  // when(item...)

            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED,
        )

    }

}