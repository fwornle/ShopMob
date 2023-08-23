package com.tanfra.shopmob.smob.ui.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.base.NavigationCommand
import com.tanfra.shopmob.databinding.ActivityDetailsBinding
import com.tanfra.shopmob.smob.data.local.RefreshLocalDB
import com.tanfra.shopmob.smob.data.repo.ato.Ato
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobProductWithListDataATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.utils.getSerializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.*
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

        // define polymorphic serializer for class "ATO"
        private val module = SerializersModule {
            polymorphic(Ato::class) {
                subclass(SmobListATO::class)
            }
        }

        // configure JSON serializer - allowing for open polymorphism
        private val mapper = Json {
            encodeDefaults = true
            classDiscriminator = "source"
            serializersModule = module
        }

        // Intent factory, used upon selection in an RV list - communicate just the item
        // ... use super type "Ato" to remain generic for all types of lists
        fun newIntent(context: Context, source: SmobDetailsSources, smobListItem: Ato): Intent {
            return context.createIntent<SmobDetailsActivity>(
                EXTRA_Source to source,
                EXTRA_SmobItem to mapper.encodeToString(smobListItem),
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


        // attempt to read extra data from incoming intent
        val extras: Bundle? = intent.extras
        extras?.let {
            // fetch incoming parameters (common: EXTRA_Source)
            if (it.containsKey(EXTRA_Source)) {
                // extract the extra-data in the intent
                _viewModel.navSource = intent.getSerializable(
                    "EXTRA_Source",
                    SmobDetailsSources::class.java
                )
            }
        }

        // navigate to the requested fragment
        when(_viewModel.navSource) {

            // home fragment or geofence
            SmobDetailsSources.PLANNING_SHOP_LIST,
            SmobDetailsSources.GEOFENCE,
            -> {

                // navigate to Shop Details fragment
                Timber.i("Display details of the selected shop.")

                extras?.let {
                    if (it.containsKey(EXTRA_SmobItem)) {
                        // extract the extra-data
                        val encString = intent.getStringExtra(EXTRA_SmobItem)

                        // store value in ViewModel
                        _viewModel.smobShopDetailsItem.value = encString?.let {
                            // https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md#sealed-classes
                            //
                            // need to use polymorphic decoder (!!) - by qualifying it with <Ato>
                            // ... as opposed to <SmobShopATO>  -->  necessary for the decoder to
                            // make sense of leading (data) type element (designator changed to
                            // 'source' to avoid clash with SmobShopATO property 'type')
                            mapper.decodeFromString<Ato>(it) as SmobShopATO
                        }
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
                        val encString = intent.getStringExtra(EXTRA_SmobItem)

                        // store value in ViewModel
                        _viewModel.smobProductDetailsItem.value = encString?.let {
                            mapper.decodeFromString<Ato>(it) as SmobProductWithListDataATO
                        }
                    }
                }

            }

            else -> Timber.i("Unknown source of navigation.")

        }  // when(intentSource)

    }  // onCreate


    override fun onResume() {
        super.onResume()
        RefreshLocalDB.timer.start()
    }

    override fun onPause() {
        super.onPause()
        RefreshLocalDB.timer.cancel()
    }

}