package com.tanfra.shopmob.smob.ui.planning.productEdit

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.databinding.FragmentPlanningProductEditBinding
import com.tanfra.shopmob.smob.data.local.utils.ActivityStatus
import com.tanfra.shopmob.smob.data.local.utils.ProductCategory
import com.tanfra.shopmob.smob.data.local.utils.ProductMainCategory
import com.tanfra.shopmob.smob.data.local.utils.ProductSubCategory
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.ui.planning.listsEdit.PlanningListsEditViewModel
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
            DataBindingUtil.inflate(inflater, R.layout.fragment_planning_product_edit, container, false)

        setDisplayHomeAsUpEnabled(true)

        // provide (injected) viewModel as data source for data binding
        binding.viewModel = _viewModel

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        // clicking on the 'selectLocation' textView takes you to the fragment "PlanningShopList"
        // for shop selection
        binding.selectLocation.setOnClickListener {
            _viewModel.navigationCommand.value =
                NavigationCommand.To(
                    PlanningProductEditFragmentDirections.actionPlanningProductEditFragmentToPlanningShopListFragment()
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

