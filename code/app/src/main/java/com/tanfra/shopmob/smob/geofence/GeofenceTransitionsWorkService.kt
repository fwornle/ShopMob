package com.tanfra.shopmob.smob.geofence

import android.content.Context
import androidx.work.*
import com.tanfra.shopmob.smob.data.local.utils.ProductMainCategory
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.ui.planning.lists.PlanningListsViewModel
import com.tanfra.shopmob.smob.ui.planning.productList.PlanningProductListViewModel
import com.tanfra.shopmob.smob.work.SmobAppWork
import com.tanfra.shopmob.utils.hasProduct
import com.tanfra.shopmob.utils.sendNotification
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class GeofenceTransitionsWorkService(val appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params), KoinComponent {

    companion object {

        const val GEOFENCE_WORK_NAME = "SMOB_GEOFENCE_WORK"
        const val GEOFENCE_EVENT_PARAM = "GEOFENCE_EVENT_PARAM"

        const val ENTRY_STRING = "transition - enter"
        const val EXIT_STRING = "transition - exit"
        const val INVALID_STRING = "nknown transition"

    }

    // get repository instance for shop
    private val _planningListsViewModel: PlanningListsViewModel by inject()
    private val _planningProductListViewModel: PlanningProductListViewModel by inject()
    private val smobAppWork: SmobAppWork by inject()

    // this will be triggered, as soon as the user enters the geoFence perimeter
    override suspend fun doWork(): Result {

        // fetch geofencingEvent from inputData structure
        val geofenceTransitionDetails: String? = inputData.getString(GEOFENCE_EVENT_PARAM)

        // anything? (should be)
        geofenceTransitionDetails?.let {

            // extract entry/exit direction and SmobShop IDs
            val transitionDir = geofenceTransitionDetails.substringBefore(':')
            val geoFenceIdList = geofenceTransitionDetails.substringAfter(':').split(',')

            when(transitionDir) {

                ENTRY_STRING -> {
                    Timber.i("Received geoFence entry transition")

                    // near a shop --> fetch all SmobLists (of the user) and determine if there are items
                    // which are of a category that matches any of the shops
//                    _planningListsViewModel.fetchSmobLists()

                    // collect list of all SmobLists
                    _planningListsViewModel.listsDataSource
                        .getAllSmobLists()
                        .collectLatest { listOfSmobLists ->

                            // check Resource status
                            when(listOfSmobLists.status) {

                                Status.ERROR -> {
                                    // these are errors handled at Room level --> display
                                    Timber.e("Cannot fetch smobLists flow: ${listOfSmobLists.message}")
                                }

                                Status.LOADING -> {
                                    // could control visibility of progress bar here
                                    Timber.i("SmobLists... still loading.")
                                }

                                Status.SUCCESS -> {
                                    // process successfully received data
                                    val smobLists = listOfSmobLists.data

                                    // make list of products (= product IDs) we're after
                                    val smobProductIds = mutableListOf<String>()

                                    // loop over all smobLists
                                    smobLists?.map {
                                        it.items
                                            .map { item -> item.id }
                                            .forEach { smobProductIds.add(it) }
                                    }

                                    // uniquify products
                                    val uniqueProductIds = smobProductIds.distinct()
                                    Timber.i("Items found (on all lists): $uniqueProductIds")

                                    // make list of main categories of our products (= what shop?)
                                    val productMainCategories = mutableListOf<ProductMainCategory>()

                                    // loop over all smobLists
                                    smobLists?.map {
                                        it.items
                                            .map { item -> item.mainCategory }
                                            .forEach { productMainCategories.add(it) }
                                    }

                                    // uniquify product main categories
                                    val uniqueMainCategories = productMainCategories.distinct()
                                    Timber.i("Items found (on all lists): $uniqueMainCategories")

                                    // now check, if we are near a shop that sells our stuff
                                    when {
                                        // sanity check
                                        geoFenceIdList.isEmpty() -> {
                                            Timber.e("Weird - received a geoFence event without triggerings --> not sending notification to user.")
//                                            return@collect Result.failure()
                                        }
                                        else -> {

                                            // valid (non-empty) geoFenceIdList received --> process

                                            // fetch shop details - collect SmobShopList flow
                                            _planningProductListViewModel.fetchSmobShopList()

                                            // process successfully received data
                                            val smobShopListRes = _planningProductListViewModel.smobShopList.value
                                            when(smobShopListRes.status) {

                                                Status.ERROR -> {
                                                    // these are errors handled at Room level --> display
                                                    Timber.e("Cannot fetch smobShops flow: ${listOfSmobLists.message}")
                                                }

                                                Status.LOADING -> {
                                                    // could control visibility of progress bar here
                                                    Timber.i("SmobShops... still loading.")
                                                }

                                                Status.SUCCESS -> {
                                                    // process successfully received data
                                                    val smobShopList = smobShopListRes.data

                                                    // loop over all geoFence IDs (= SmobShop IDs)
                                                    geoFenceIdList.map { geoFenceItem ->

                                                        // fetch shop details
                                                        val smobShoppe = smobShopList?.find { it?.id == geoFenceItem }
                                                        smobShoppe?.let { daShop ->

                                                            if(uniqueMainCategories.any { daShop.hasProduct(it) }) {
                                                                // they seem to do --> notify user
                                                                sendNotification(appContext, daShop)
                                                            }

                                                        }

                                                    }  // loop over all geoFenceIds

                                                }  // Status.SUCCESS (SmobShops)

                                            } // when (smobShopsRes.status)

                                        }  // some valid geoFenceIds

                                    }  // when geoFenceId

                                }  // Status.SUCCESS (SmobLists)

                            }  // when (Resource status, SmobLists)

                        }  // collect flow (SmobLists)

                }

                EXIT_STRING -> {
                    // currently unused
                    Timber.i("Received geoFence exit transition")
                    return Result.failure()
                }

                INVALID_STRING -> {
                    // should not happen...
                    Timber.i("Received invalid geoFence transition type")
                    return Result.failure()
                }

            }  // when

        }  //

        // completed geoFence work successfully
        return Result.success()

    }

}  // class GeofenceTransitionsJobIntentService