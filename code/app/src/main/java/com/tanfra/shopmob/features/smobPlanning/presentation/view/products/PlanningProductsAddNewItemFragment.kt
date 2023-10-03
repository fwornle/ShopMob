package com.tanfra.shopmob.features.smobPlanning.presentation.view.products

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.viewModelScope
import com.tanfra.shopmob.R
import com.tanfra.shopmob.databinding.FragmentPlanningProductsAddNewItemBinding
import com.tanfra.shopmob.smob.data.types.ActivityStatus
import com.tanfra.shopmob.smob.data.types.InShop
import com.tanfra.shopmob.smob.data.types.ItemStatus
import com.tanfra.shopmob.smob.data.types.ProductCategory
import com.tanfra.shopmob.smob.data.types.ProductMainCategory
import com.tanfra.shopmob.smob.data.types.ProductSubCategory
import com.tanfra.shopmob.smob.data.types.ShopCategory
import com.tanfra.shopmob.smob.data.types.ShopLocation
import com.tanfra.shopmob.smob.data.types.SmobListItem
import com.tanfra.shopmob.smob.data.types.SmobListLifecycle
import com.tanfra.shopmob.smob.ui.zeUiBase.BaseFragment
import com.tanfra.shopmob.app.utils.setDisplayHomeAsUpEnabled
import com.tanfra.shopmob.smob.data.local.utils.*
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationCommand
import com.tanfra.shopmob.features.smobPlanning.presentation.PlanningViewModel
import com.tanfra.shopmob.smob.ui.zeUtils.closeSoftKeyboard
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent
import timber.log.Timber
import java.util.*
import kotlin.math.roundToInt

@SuppressLint("UnspecifiedImmutableFlag")
class PlanningProductsAddNewItemFragment :
    BaseFragment(), AdapterView.OnItemSelectedListener, KoinComponent {

    // get the view model (from Koin) this time as a singleton to be shared with another fragment
    override val viewModel: PlanningViewModel by activityViewModel()

    // data binding of underlying layout
    private lateinit var binding: FragmentPlanningProductsAddNewItemBinding

    // assemble smob data item - this creates the ID we can use as geoFence ID
    private lateinit var daSmobProductATO: SmobProductATO


    // create fragment view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // inflate fragment layout and return binding object
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_planning_products_add_new_item,
                container,
                false
            )

        setDisplayHomeAsUpEnabled(true)

        // provide (injected) viewModel as data source for data binding
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        // clicking on the 'selectLocation' textView takes you to the fragment "PlanningShopList"
        // for shop selection
        binding.selectShop.setOnClickListener {

            // set navigation source
            viewModel.navSource = "planningProductEditFragment"

            viewModel.navigationCommand.value =
                NavigationCommand.To(
                    PlanningProductsAddNewItemFragmentDirections
                        .actionSmobPlanningProductsAddNewItemFragmentToSmobPlanningShopsTableFragment()
                )
        }

        // set-up spinners
        setupSpinners()

        // handler for 'SAVE' FAB clicks...
        binding.saveSmobItem.setOnClickListener {

            // close SoftKeyboard
            closeSoftKeyboard(requireContext(), view)

            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.GERMANY)
            val currentDate = sdf.format(Date())

            // collect (state)flow smobListSF to be able to add a new product to the (currently
            // selected) SmobList
            var currList: SmobListATO? = null
            var valItems: List<SmobListItem>? = null
            var nValItems = 0
            var itemMaxPosition = 0L

            viewModel.viewModelScope.launch {
                viewModel.smobListSF.take(1).collect {

                    // valid data? (making sure...)
                    when (it) {
                        is Resource.Failure -> Timber.i("Couldn't retrieve SmobList from remote")
                        is Resource.Empty -> Timber.i("SmobList still loading")
                        is Resource.Success -> {

                            // store current SmobList
                            currList = it.data

                            valItems =
                                it.data.items.filter { itm -> itm.status != ItemStatus.DELETED }
                            nValItems = valItems?.size ?: 0
                            itemMaxPosition = currList?.items?.fold(0L) { max, item ->
                                if (item.listPosition > max) {
                                    item.listPosition
                                } else {
                                    max
                                }
                            } ?: 0L

                        }  // Resource.Success
                    }  // when

                }  // collect
            }  // coroutine


            // initialize data record to be written to DB
            // ... if no better values have been provided by the user (taken from viewModel), this
            //     is going to be the data record written to the DB
            daSmobProductATO = SmobProductATO(
                UUID.randomUUID().toString(),
                ItemStatus.OPEN,
                itemMaxPosition + 1,
                viewModel.smobProductName.value ?: "",
                viewModel.smobProductDescription.value ?: "",
                viewModel.smobProductImageUrl.value ?: "",
                viewModel.smobProductCategory.value
                    ?: ProductCategory(
                        ProductMainCategory.OTHER,
                        ProductSubCategory.OTHER
                    ),
                ActivityStatus(currentDate, 0),
                viewModel.selectedShop.value?.let { it1 ->
                    InShop(
                        it1.category,
                        viewModel.selectedShop.value!!.name,
                        viewModel.selectedShop.value!!.location,
                    )
                } ?: InShop(
                    ShopCategory.OTHER,
                    "mystery shop",
                    ShopLocation(0.0, 0.0)
                ),
            )

            // store smob product in DB
            // ... this also takes the user back to the SmobProductListFragment
            viewModel.validateAndSaveSmobItem(daSmobProductATO)


            // update statistics on shopping list
            currList?.let {

                // add smob item to the currently open shopping list
                val newItems = it.items.toMutableList()
                newItems.add(
                    SmobListItem(
                        daSmobProductATO.id,
                        daSmobProductATO.status,
                        daSmobProductATO.position,
                        daSmobProductATO.category.main,
                    )
                )

                // create updated smobList (to be sent to the DB/backend)
                val newList = SmobListATO(
                        it.id,
                        it.status,
                        it.position,
                        it.name,
                        it.description,
                        newItems,
                        it.groups,
                        SmobListLifecycle(
                            if (it.lifecycle.status.ordinal <= ItemStatus.OPEN.ordinal) {
                                ItemStatus.OPEN
                            } else {
                                it.lifecycle.status
                            },
                            when (nValItems) {
                                0 -> 0.0
                                else -> {
                                    val doneItems = valItems
                                        ?.filter { daItem -> daItem.status == ItemStatus.DONE }?.size
                                        ?: 0
                                    (100.0 * doneItems / nValItems).roundToInt().toDouble()
                                }
                            }
                        ),
                    )

                // store new List in DB - no need to trigger back navigation (already triggered
                // when saving the product in the local DB
                viewModel.saveSmobListItem(newList, false)

            }  // smobList == null?

        }  // onClickListener (FAB - save)

    }  // onViewCreated


    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        viewModel.onClearProduct()
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
                viewModel.smobProductCategory.value?.main = ProductMainCategory.values()[p2]
            }
            binding.smobItemSubCategory -> {
                // set main product category
                viewModel.smobProductCategory.value?.sub = ProductSubCategory.values()[p2]
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

