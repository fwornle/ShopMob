package com.tanfra.shopmob.smob.ui.planning.shopEdit

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.Geofence.NEVER_EXPIRE
import com.google.android.material.snackbar.Snackbar
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import timber.log.Timber
import com.google.android.gms.location.LocationServices
import com.tanfra.shopmob.databinding.FragmentPlanningShopEditBinding
import com.tanfra.shopmob.smob.data.local.utils.*
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.ui.planning.utils.closeSoftKeyboard
import com.tanfra.shopmob.smob.work.SmobAppWork
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*


@SuppressLint("UnspecifiedImmutableFlag")
class PlanningShopEditFragment : BaseFragment(), AdapterView.OnItemSelectedListener, KoinComponent {

    // get the view model (from Koin) this time as a singleton to be shared with another fragment
    override val _viewModel: PlanningShopEditViewModel by sharedViewModel()

    // data binding of underlying layout
    private lateinit var binding: FragmentPlanningShopEditBinding

    // assemble smob data item - this creates the ID we can use as geoFence ID
    private lateinit var daSmobShopATO: SmobShopATO


    // create fragment view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // inflate fragment layout and return binding object
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_planning_shop_edit, container, false)

        setDisplayHomeAsUpEnabled(true)

        // provide (injected) viewModel as data source for data binding
        binding.viewModel = _viewModel

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        // clicking on the 'selectLocation' textView takes you to the fragment "select location"
        // ... by means of the observer function of MutableLiveData element 'navigationCommand'
        //     --> see BaseFragment.kt... where the observer (lambda) is installed
        binding.defineLocation.setOnClickListener {

        }

        // configure spinner (shop category)
        setupSpinners()

        // clicking on the 'saveSmobShop' FAB...
        // ... installs the geofencing request and triggers the saving to DB
        binding.saveSmobShop.setOnClickListener {

            // close SoftKeyboard
            closeSoftKeyboard(requireContext(), view)

            // initialize data record to be written to DB
            // ... if no better values have been provided by the user (taken from viewModel), this
            //     is going to be the data record written to the DB
            daSmobShopATO = SmobShopATO(
                UUID.randomUUID().toString(),
                SmobItemStatus.NEW,
                -1L,
                _viewModel.locatedShop.value?.name ?: "mystery shop",
                _viewModel.locatedShop.value?.description ?: "something strange",
                _viewModel.locatedShop.value?.imageUrl ?: "some mystery picture",
                _viewModel.locatedShop.value?.location ?: ShopLocation(0.0, 0.0),
                _viewModel.locatedShop.value?.type ?: ShopType.CHAIN,
                _viewModel.locatedShop.value?.category ?: ShopCategory.SUPERMARKET,
                _viewModel.locatedShop.value?.business ?: listOf(
                    "Monday: closed",
                    "Tuesday: closed",
                    "Wednesday: closed",
                    "Thursday: closed",
                    "Friday: closed",
                    "Saturday: closed",
                    "Sunday: closed",
                ),
            )

            // store smob item in DB
            // ... this also takes the user back to the SmobItemListFragment
            _viewModel.validateAndSaveSmobItem(daSmobShopATO)

            // continue flow - (will now omit the geoFencing part)
            // fetch WorkManager instance from Koin service locator
            val workManager: SmobAppWork by inject()
            // extract SmobShop IDs and turn into a JSON string (ready for transmission to
            // the WorkManager "doWork" job via (string) parameter
            val geofenceTransitionDetails = "transition - enter: ${daSmobShopATO.id}"

            // schedule background work (WorkManager), handling potential geofencing entry
            // transition events
            val geoFenceWorkRequest = workManager
                .setupOnTimeJobForGeoFenceNotification(geofenceTransitionDetails)

            // schedule background job
            workManager.scheduleUniqueWorkForGeoFenceNotification(geoFenceWorkRequest)

            Toast.makeText(activity, "for udacity: simulated geoFence triggered workManager", Toast.LENGTH_SHORT).show()

        }  // onClickListener (FAB - save)

    }  // onViewCreated


    // set-up spinners (categories)
    private fun setupSpinners() {

        // ShopCategory
        // create an ArrayAdapter using the enum and a default spinner layout
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ShopCategory.values())
            .also {
                // specify the layout to use when the list of choices appears
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // apply the adapter to the spinner
                binding.smobShopCategory.adapter = it
            }

        // hook up onItemSelected listener (to this fragment)
        binding.smobShopCategory.onItemSelectedListener = this

    }

    // spinner: item selection made
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        // we might have several spinners at some point...
        when(p0) {
            binding.smobShopCategory -> {
                // set shop category in VM
                _viewModel.locatedShop.value?.category = ShopCategory.values()[p2]
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

}

