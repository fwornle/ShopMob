package com.tanfra.shopmob.smob.activities.planning.productEdit

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.tanfra.shopmob.base.BaseFragment
import com.tanfra.shopmob.base.NavigationCommand
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import timber.log.Timber
import com.google.android.gms.location.LocationServices
import com.tanfra.shopmob.databinding.FragmentPlanningProductEditBinding
import com.tanfra.shopmob.smob.data.local.utils.ActivityStatus
import com.tanfra.shopmob.smob.data.local.utils.ProductCategory
import com.tanfra.shopmob.smob.data.local.utils.ProductMainCategory
import com.tanfra.shopmob.smob.data.local.utils.ProductSubCategory
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import java.util.*


@SuppressLint("UnspecifiedImmutableFlag")
class PlanningProductEditFragment : BaseFragment(), KoinComponent {

    // get the view model (from Koin) this time as a singleton to be shared with another fragment
    override val _viewModel: PlanningProductEditViewModel by viewModel()

    // data binding of underlying layout
    private lateinit var binding: FragmentPlanningProductEditBinding

    // assemble smob data item - this creates the ID we can use as geoFence ID
    private lateinit var daSmobProductATO: SmobProductATO


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
        binding.lifecycleOwner = this

        // clicking on the 'selectLocation' textView takes you to the fragment "PlanningProductList"
        // ... by means of the observer function of MutableLiveData element 'navigationCommand'
        //     --> see BaseFragment.kt... where the observer (lambda) is installed
        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(
                    PlanningProductEditFragmentDirections.actionPlanningProductEditFragmentToPlanningProductListFragment()
                )
        }

        // clicking on the 'saveSmobItem' FAB...
        // ... installs the geofencing request and triggers the saving to DB
        binding.saveSmobItem.setOnClickListener {

            // initialize data record to be written to DB
            // ... if no better values have been provided by the user (taken from viewModel), this
            //     is going to be the data record written to the DB
            daSmobProductATO = SmobProductATO(
                UUID.randomUUID().toString(),
                _viewModel.smobProductName.value ?: "mystery item",
                _viewModel.smobProductDescription.value ?: "something strange",
                _viewModel.smobProductImageUrl.value ?: "mystery picture",
                _viewModel.smobProductCategory.value
                    ?: ProductCategory(ProductMainCategory.OTHER, ProductSubCategory.OTHER),
                _viewModel.smobProductActivity.value
                    ?: ActivityStatus("this is now...", 0),
            )

            // store smob product in DB
            // ... this also takes the user back to the SmobProductListFragment
            _viewModel.validateAndSaveSmobItem(daSmobProductATO)

        }  // onClickListener (FAB - save)

    }  // onViewCreated


    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }

}

