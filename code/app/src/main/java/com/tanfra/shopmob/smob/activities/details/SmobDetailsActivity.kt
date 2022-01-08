package com.tanfra.shopmob.smob.activities.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.SmobApp
import com.tanfra.shopmob.base.NavigationCommand
import com.tanfra.shopmob.databinding.ActivityDetailsBinding
import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductOnListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import timber.log.Timber


/**
 * Activity that displays the smob item details after the user clicks on a list item
 */
class SmobDetailsActivity : AppCompatActivity(), KoinComponent {

    // Intent gateway for lists which want their content to be displayed (generic details screen)
    companion object{
        private const val EXTRA_Source = "EXTRA_Source"
        private const val EXTRA_SmobItem = "EXTRA_SmobItem"

        // Intent factory, used upon selection in an RV list - communicate just the item
        // ... use super type "Ato" to remain generic for all types of lists
        fun newIntent(context: Context, source: SmobDetailsSources, smobListItem: Ato): Intent {
            return context.createIntent<SmobDetailsActivity>(
                EXTRA_Source to source,
                EXTRA_SmobItem to smobListItem,
            )
        }
    }


    // use Koin service locator to retrieve the ViewModel instance
    val _viewModel: DetailsViewModel by viewModel()

    // data binding
    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflate layout
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_details
        )

        // try to fetch data provided by intent source
        var intentSource = SmobDetailsSources.UNKNOWN

        // attempt to read extra data from incoming intent
        val extras: Bundle? = intent.extras
        extras?.let {
            if (it.containsKey(EXTRA_Source)) {
                // extract the extra-data in the intent
                intentSource = it.get("EXTRA_Source") as SmobDetailsSources
            }
        }


        // navigate to the requested fragment
        when(intentSource) {

            // home fragment
            SmobDetailsSources.PLANNING_SHOP_LIST -> {

                // navigate to Shop Details fragment
                Timber.i("Display details of the selected shop.")

                extras?.let {
                    if (it.containsKey(EXTRA_SmobItem)) {
                        // extract the extra-data
                        val smobListItem = it.getSerializable("EXTRA_SmobItem") as SmobShopATO

                        // store value in ViewModel
                        _viewModel.smobShopDetailsItem.value = smobListItem
                    }
                }

            }

            SmobDetailsSources.PLANNING_PRODUCT_LIST -> {

                // navigate to Product Details fragment
                Timber.i("Display details of the selected product.")


                // navigate to Product Details fragment
                // ... note: SmobDetailsActivity starts up with Shop Details fragment
                // ... use the navigationCommand live data to navigate between the fragments
                _viewModel.navigationCommand.postValue(
                    NavigationCommand.To(
                        DetailsShopFragmentDirections.actionSmobDetailsShopFragmentToSmobDetailsProductFragment()
                    )
                )

                extras?.let {
                    if (it.containsKey(EXTRA_SmobItem)) {
                        // extract the extra-data
                        val smobListItem = it.getSerializable("EXTRA_SmobItem") as SmobProductOnListATO

                        // store value in ViewModel
                        _viewModel.smobProductDetailsItem.value = smobListItem
                    }
                }

            }

            else -> Timber.i("Unknown source of navigation.")

        }  // when(intentSource)

    }  // onCreate

    override fun onStart() {
        super.onStart()
        SmobApp.scheduleRecurringWorkFast()
    }

    override fun onStop() {
        super.onStop()
        SmobApp.cancelRecurringWorkFast()
    }

}