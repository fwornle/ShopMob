package com.tanfra.shopmob.smob.geofence

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobItemDataSource
import com.tanfra.shopmob.smob.data.repo.Result
import com.tanfra.shopmob.smob.types.SmobItem
import com.tanfra.shopmob.utils.sendNotification
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class GeofenceTransitionsJobIntentService : JobIntentService(), CoroutineScope {

    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    companion object {
        private const val JOB_ID = 573

        // start the JobIntentService to handle the geofencing transition events
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                context,
                GeofenceTransitionsJobIntentService::class.java, JOB_ID,
                intent
            )
        }
    }

    // this will be triggered, as soon as the user enters the geoFence perimeter
    override fun onHandleWork(intent: Intent) {

        // Get the geoFences that were triggered
        // (note: a single event can trigger multiple geoFences)
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        val triggeringGeofences = geofencingEvent.triggeringGeofences

        // send notification with the triggering geoFences
        // note: polymorphism
        //       --> call-up parameter is a list of geoFences
        //       --> local implementation of sendNotificatino is used (see below)
        sendNotification(triggeringGeofences)

    }

    // fetch ID associated with triggering geoFence (coincides with smob item ID in DB)
    private fun sendNotification(triggeringGeofences: List<Geofence>) {

        // sanity check
        when {

            triggeringGeofences.isEmpty() -> {
                Timber.e("Weird - received a geoFence event without triggerings --> not sending notification to user.")
                return
            }
            else -> {

                // everything normal (some triggered geoFences found)
                // --> fetch data and send notification

                // loop over all triggered geoFences at this location
                for (geoFenceItem in triggeringGeofences) {

                    // get the local repository instance
                    val smobItemLocalRepository: SmobItemDataSource by inject()

                    // ... interaction to the repository has to be through a coroutine scope
                    CoroutineScope(coroutineContext).launch(SupervisorJob()) {

                        // get the smob item with the request id
                        val result = smobItemLocalRepository.getSmobItem(geoFenceItem.requestId)

                        // smob location found in DB?
                        if (result is Result.Success<SmobItem>) {

                            // yes --> fetch associated smob item data
                            //         ... and send it down the notification channel
                            val smobItem = result.data

                            // send a notification to the user with the smob item details
                            // note: polymorphism
                            //       --> call-up parameter is a SmobItem
                            //       --> implementation of sendNotificatino from NotificationUtils.kt is used
                            sendNotification(
                                this@GeofenceTransitionsJobIntentService, SmobItem(
                                    smobItem.title,
                                    smobItem.description,
                                    smobItem.location,
                                    smobItem.latitude,
                                    smobItem.longitude,
                                    smobItem.id
                                )
                            )

                        }  // if (smob location found in DB)

                    }  // Coroutine scope

                }  // loop over all geoFences at this location

            }  // sanity check: normal case

        }  // when

    }  // sendNotification

}  // class GeofenceTransitionsJobIntentService