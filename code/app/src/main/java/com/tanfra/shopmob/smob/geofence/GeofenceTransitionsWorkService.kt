package com.tanfra.shopmob.smob.geofence

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.asLiveData
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobShopDataSource
import com.tanfra.shopmob.smob.data.repo.utils.Resource
import com.tanfra.shopmob.smob.data.repo.utils.Status
import com.tanfra.shopmob.smob.ui.base.BaseRecyclerViewAdapter
import com.tanfra.shopmob.smob.work.SmobAppWork
import com.tanfra.shopmob.utils.sendNotification
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response
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
    val smobShopDataSource: SmobShopDataSource by inject()
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
                            for (geoFenceItem in geoFenceIdList) {

                                // get the SmobShop with id from geoFence
                                val daFlow = smobShopDataSource.getSmobShop(geoFenceItem)
                                    daFlow.collect {
                                        Timber.i("Received shop: ${it.data?.name}")

                                        // send notification with the triggering geoFences
                                        // note: polymorphism
                                        //       --> call-up parameter is a list of geoFence IDs (= SmobShop IDs)
                                        //       --> local implementation of sendNotificatino is used (see below)
                                        sendNotification(it.data)
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