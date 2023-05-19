package com.tanfra.shopmob.smob.geofence

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tanfra.shopmob.smob.data.local.utils.ProductMainCategory
import com.tanfra.shopmob.smob.data.repo.ato.SmobListATO
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.ui.planning.PlanningViewModel
import com.tanfra.shopmob.utils.ui.hasProduct
import com.tanfra.shopmob.utils.sendNotificationOnGeofenceHit
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
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
        const val INVALID_STRING = "invalid transition"
    }

    // get repository instance for shop
    private val _planningViewModel: PlanningViewModel by inject()

    // this will be triggered, as soon as the user enters the geoFence perimeter
    override suspend fun doWork(): Result {

        // fetch geofencingEvent from inputData structure
        val geofenceTransitionDetails: String? = inputData.getString(GEOFENCE_EVENT_PARAM)

        // local helper function: extract all "main categories" (SUPERMARKET, ...)
        // ... from an aggregated list of products of a given set of SmobList (shopping) lists
        fun extractUniqueMainCategories(smobLists: List<SmobListATO>?): List<ProductMainCategory> {
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
            return uniqueMainCategories
        }


        // analyze...
        geofenceTransitionDetails?.let {

            // extract entry/exit direction and SmobShop IDs
            val transitionDir = geofenceTransitionDetails.substringBefore(':')
            val geoFenceIdList = geofenceTransitionDetails.substringAfter(':')
                .replace(" ", "").split(',')

            when(transitionDir) {

                ENTRY_STRING -> {
                    Timber.i("Received geoFence entry transition")

                    // near a shop --> fetch all SmobLists (of the user) and determine if there are items
                    // which are of a category that matches any of the shops
//                    _planningListsViewModel.fetchSmobLists()

                    // debugging
                    var flowCollections = 0

                    // SmobLists, collected from local DB
                    var smobLists: List<SmobListATO>? = null

                    // collect flow (originating in the Room DB) of list of all SmobLists
                    _planningViewModel.listDataSource
                        .getAllSmobItems()
                        .take(1)
                        .onEach { Timber.i("collecting SmobList resource with status: ${it.status}, fc: ${flowCollections++}") }
                        .collect { listOfSmobLists ->

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
                                    smobLists = listOfSmobLists.data

                                }  // Status.SUCCESS (SmobLists)

                            }  // when (Resource status, SmobLists)

                        }  // collect flow (SmobLists)

                    // any SmobList items?
                    smobLists?.let {

                        // yes --> reduce list to it's main categories (SUPERMARKET, ...)
                        val uniqueMainCategories =
                            extractUniqueMainCategories(smobLists)

                        // now check, if we are near a shop that sells our stuff
                        when {
                            // sanity check
                            geoFenceIdList.isEmpty() -> {
                                Timber.e("Weird - received a geoFence event without triggerings --> not sending notification to user.")
//                                            return@collect Result.failure()
                            }
                            else -> {

                                // valid (non-empty) geoFenceIdList received --> process

                                // SmobShops, collected from local DB
                                var smobShops: List<SmobShopATO>? = null

                                // fetch smobShops
                                // collect flow (originating in the Room DB) of list of all SmobShops
                                _planningViewModel.shopDataSource
                                    .getAllSmobItems()
                                    .take(1)
                                    .onEach { Timber.i("collecting SmobShop resource with status: ${it.status}, fc: ${flowCollections++}") }
                                    .collect { listOfSmobShops ->

                                        // check Resource status
                                        when(listOfSmobShops.status) {

                                            Status.ERROR -> {
                                                // these are errors handled at Room level --> display
                                                Timber.e("Cannot fetch smobShops flow: ${listOfSmobShops.message}")
                                            }

                                            Status.LOADING -> {
                                                // could control visibility of progress bar here
                                                Timber.i("SmobShops... still loading.")
                                            }

                                            Status.SUCCESS -> {
                                                // process successfully received data
                                                smobShops = listOfSmobShops.data

                                            }  // Status.SUCCESS (SmobShops)

                                        }  // when (Resource status, SmobShops)

                                    }  // collect flow (SmobShops)


                                // loop over all geoFence IDs (= SmobShop IDs)
                                geoFenceIdList.map { geoFenceItem ->

                                    // fetch shop details
                                    val smobShoppe = smobShops?.find { it.itemId == geoFenceItem }
                                    smobShoppe?.let { daShop ->

                                        if(uniqueMainCategories.any { daShop.hasProduct(it) }) {
                                            // they seem to do --> notify user
                                            sendNotificationOnGeofenceHit(appContext, daShop)
                                        }

                                    }

                                }  // loop over all geoFenceIds

                            }  // some valid geoFenceIds

                        }  // when geoFenceId

                    }  // SmobLists != null

                }  // ENTRY_STRING

                EXIT_STRING -> {
                    // currently unused
                    Timber.i("Received geoFence exit transition")
                    return Result.failure()
                }

                // INVALID_STRING
                else -> {
                    // should not happen...
                    Timber.i("Received invalid geoFence transition type")
                    return Result.failure()
                }

            }  // when

        }  // geofenceTransitionDetails != null

        // completed geoFence work successfully
        return Result.success()

    }

}  // class GeofenceTransitionsJobIntentService