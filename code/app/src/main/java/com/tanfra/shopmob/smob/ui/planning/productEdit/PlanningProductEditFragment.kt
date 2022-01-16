package com.tanfra.shopmob.smob.ui.planning.productEdit

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.base.BaseFragment
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import com.tanfra.shopmob.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.databinding.FragmentPlanningProductEditBinding
import com.tanfra.shopmob.smob.data.local.utils.*
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.ui.planning.productList.PlanningProductListViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinComponent
import timber.log.Timber
import java.lang.Double.NaN
import java.util.*
import kotlin.math.roundToInt


@SuppressLint("UnspecifiedImmutableFlag")
class PlanningProductEditFragment : BaseFragment(), AdapterView.OnItemSelectedListener, KoinComponent {

    // get the view model (from Koin) this time as a singleton to be shared with another fragment
    override val _viewModel: PlanningProductListViewModel by sharedViewModel()

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
        binding.selectShop.setOnClickListener {
            _viewModel.navigationCommand.value =
                NavigationCommand.To(
                    PlanningProductEditFragmentDirections.actionPlanningProductEditFragmentToPlanningShopListFragment()
                )
        }

        // set-up spinners
        setupSpinners()


        // handler for 'SAVE' FAB clicks...
        binding.saveSmobItem.setOnClickListener {

            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.GERMANY)
            val currentDate = sdf.format(Date())

            // fetch items on current shopping list
            val currList = _viewModel.smobList.value.data
            val valItems = currList?.items?.filter { itm -> itm.status != SmobItemStatus.DELETED }
            val nValItems = valItems?.size ?: 0
            val itemMaxPosition = currList?.items?.fold(0L) { max, item ->
                if(item.listPosition > max) { item.listPosition } else { max }
            } ?: 0L


            // initialize data record to be written to DB
            // ... if no better values have been provided by the user (taken from viewModel), this
            //     is going to be the data record written to the DB
            daSmobProductATO = SmobProductATO(
                UUID.randomUUID().toString(),
                SmobItemStatus.NEW,
                itemMaxPosition + 1,
                _viewModel.smobProductName.value ?: "",
                _viewModel.smobProductDescription.value ?: "",
                _viewModel.smobProductImageUrl.value ?: "",
                _viewModel.smobProductCategory.value
                    ?: ProductCategory(ProductMainCategory.OTHER, ProductSubCategory.OTHER),
                ActivityStatus(currentDate, 0),
                _viewModel.selectedShop.value?.let { it1 ->
                    InShop(
                        it1.category,
                        _viewModel.selectedShop.value!!.name,
                        _viewModel.selectedShop.value!!.location,
                    )
                } ?: InShop(ShopCategory.OTHER, "mystery shop", ShopLocation(NaN, NaN)),
            )

            // store smob product in DB
            // ... this also takes the user back to the SmobProductListFragment
            _viewModel.validateAndSaveSmobItem(daSmobProductATO)

            // add smob item to the currently open shopping list
            val newItems = currList?.items?.toMutableList() ?: mutableListOf()
            newItems.add(
                SmobListItem(
                    daSmobProductATO.id,
                    daSmobProductATO.itemStatus,
                    daSmobProductATO.itemPosition,
                )
            )

            // create updated list (to be sent to the DB/backend)
            val newList = SmobListATO(
                currList!!.id,
                currList.itemStatus,
                currList.itemPosition,
                currList.name,
                currList.description,
                newItems,
                currList.members,
                SmobListLifecycle(
                    if(currList.lifecycle.status.ordinal <= SmobItemStatus.OPEN.ordinal) {
                        SmobItemStatus.OPEN
                    } else {
                        currList.lifecycle.status
                    },
                    when(nValItems) {
                        0 -> 0.0
                        else -> {
                            val doneItems = valItems!!.filter { daItem -> daItem.status == SmobItemStatus.DONE }.size
                            (100.0 * doneItems / nValItems).roundToInt().toDouble()
                        }
                    }
                ),
            )

            // store new List in DB
            _viewModel.saveSmobListItem(newList)

        }  // onClickListener (FAB - save)

    }  // onViewCreated


    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }

    // set-up spinners (categories)
    private fun setupSpinners() {

        // ProductMainCategory
        // create an ArrayAdapter using the enum and a default spinner layout
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ProductMainCategory.values())
            .also {
                // specify the layout to use when the list of choices appears
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // apply the adapter to the spinner
                binding.smobItemMainCategory.adapter = it
            }

        // hook up onItemSelected listener (to this fragment)
        binding.smobItemMainCategory.onItemSelectedListener = this


        // ProductSubCategory
        // create an ArrayAdapter using the enum and a default spinner layout
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ProductSubCategory.values())
            .also {
                // specify the layout to use when the list of choices appears
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // apply the adapter to the spinner
                binding.smobItemSubCategory.adapter = it
            }

        // hook up onItemSelected listener (to this fragment)
        binding.smobItemSubCategory.onItemSelectedListener = this

    }

    // spinner: item selection made
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        // we have several spinners...
        when(p0) {
            binding.smobItemMainCategory -> {
                // set main product category
                _viewModel.smobProductCategory.value?.main = ProductMainCategory.values()[p2]
            }
            binding.smobItemSubCategory -> {
                // set main product category
                _viewModel.smobProductCategory.value?.sub = ProductSubCategory.values()[p2]
            }
            else -> {
                // should not happen - unless someone added a third spinner
                Timber.i("Unknown spinner found: $p0")
            }
        }
    }

    // spinner: no item selected
    override fun onNothingSelected(p0: AdapterView<*>?) {
        Timber.i("No selection made... (this is never called?!) $p0")
    }

}

