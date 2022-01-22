package com.tanfra.shopmob.smob.geofence

import android.content.Context
import androidx.work.*
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.ui.planning.lists.PlanningListsViewModel
import com.tanfra.shopmob.smob.work.SmobAppWork
import com.tanfra.shopmob.utils.sendNotification
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
    val _viewModel: PlanningListsViewModel by inject()
    val smobAppWork: SmobAppWork by inject()

    // this will be triggered, as soon as the user enters the geoFence perimeter
    override suspend fun doWork(): Result {

        // fetch geofencingEvent from inputData structure
        val geofenceTransitionDetails: String? = inputData.getString(GEOFENCE_EVENT_PARAM)

        // anything? (should be)
        geofenceTransitionDetails?.let {

            // extract entry/exit direction and SmobShop IDs
            val transitionDir = geofenceTransitionDetails.substringBefore(':')
            val geoFenceIdList = geofenceTransitionDetails.substringAfter(':').split(',')

            _viewModel.fetchSmobLists()
            _viewModel.getSmobLists().value.data?.map {
                Timber.i("Got list ${it?.id ?: "VOID"}")
            }


            when(transitionDir) {

                ENTRY_STRING -> {
                    Timber.i("Received geoFence entry transition")

                    // sanity check
                    when {
                        geoFenceIdList.isEmpty() -> {
                            Timber.e("Weird - received a geoFence event without triggerings --> not sending notification to user.")
                            return Result.failure()
                        }
                        else -> {

                            // loop over all geoFence IDs (= SmobShop IDs)
                            // update StateFlow value from local DB
                            _viewModel.fetchSmobLists()

                            for (geoFenceItem in geoFenceIdList) {

                                Timber.i("Got GeoFence ID: $geoFenceItem")

                                // now check, if any of the triggered geoFences relates to any of
                                // our shopping lists
                                _viewModel.getSmobLists().value.data?.map {
                                    Timber.i("Got list ${it?.id ?: "VOID"}")

                                    // send notification with the triggering geoFences
                                    // note: polymorphism
                                    //       --> call-up parameter is a list of geoFence IDs (= SmobShop IDs)
                                    //       --> local implementation of sendNotificatino is used (see below)
//                                    sendNotification(it)
                                }

                            }
                        }
                    }
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


    // fetch ID associated with triggering geoFence (coincides with SmobShop ID in DB)
    private fun sendNotification(smobShopId: SmobShopATO?) {

        // send a notification to the user with the smob item details
        // note: polymorphism
        //       --> call-up parameter is a SmobItem
        //       --> implementation of sendNotification from NotificationUtils.kt is used
        smobShopId?.let { sendNotification(appContext, it) }

    }  // sendNotification

}  // class GeofenceTransitionsJobIntentService