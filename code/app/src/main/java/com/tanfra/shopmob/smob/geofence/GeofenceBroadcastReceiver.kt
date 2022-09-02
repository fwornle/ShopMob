package com.tanfra.shopmob.smob.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.tanfra.shopmob.smob.ui.planning.shops.addNewItem.PlanningShopsAddNewItemFragment.Companion.ACTION_GEOFENCE_EVENT
import com.tanfra.shopmob.smob.work.SmobAppWork
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

/**
 * Triggered by the Geofence.  Since we can have many geoFences at once, we pull the request
 * ID from the first Geofence, and locate it within the cached data in our Room DB
 */
class GeofenceBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    // fetch WorkManager instance from Koin service locator
    private val workManager: SmobAppWork by inject()

    // adapted from: https://developer.android.com/training/location/geofencing
    override fun onReceive(context: Context, intent: Intent) {

        // does this Broadcast reception concern "GeoFencing" at all?
        if (intent.action == ACTION_GEOFENCE_EVENT) {

            // yup --> grab the geoFencing event object from provided intent
            val geofencingEvent = GeofencingEvent.fromIntent(intent)

            // received a geoFenceing event at all?
            geofencingEvent?.let { gfEvent ->

                // yes --> but only process valid events...
                if (gfEvent.hasError()) {
                    val errorMessage = GeofenceStatusCodes
                        .getStatusCodeString(gfEvent.errorCode)

                    Timber.e(errorMessage)
                    return
                }


                // fetch transition type
                val geofenceTransition = gfEvent.geofenceTransition

                // ... only looking for ENTER geoFence transitions:
                when (geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER,
                        // Geofence.GEOFENCE_TRANSITION_EXIT,
                    -> {

                        // fetch all geoFences that have been triggered
                        // ... note: a single event can trigger multiple geoFences
                        val triggeringGeofences = gfEvent.triggeringGeofences?.toList() ?: listOf()

                        // extract SmobShop IDs and turn into a JSON string (ready for transmission to
                        // the WorkManager "doWork" job via (string) parameter
                        val geofenceTransitionDetails = getGeofenceTransitionDetails(
                            geofenceTransition,    // allow to distinguish between ENTER and EXIT
                            triggeringGeofences,   // the SmobShop IDs
                        )

                        // schedule background work (WorkManager), handling potential geofencing entry
                        // transition events
                        val geoFenceWorkRequest = workManager
                            .setupOnTimeJobForGeoFenceNotification(geofenceTransitionDetails)

                        // schedule background job
                        workManager.scheduleUniqueWorkForGeoFenceNotification(geoFenceWorkRequest)

                    }
                    else -> {
                        // log the unhandled transition
                        Timber.i("Encountered unhandled geoFence transition (e.g. EXIT, DWELL, ...).")
                    }

                }  // when

            }  // let

        }  // if (ACTION_GEOFENCE_EVENT)

    }  // onReceive


    // extract geoFence information and return as String (to be transmitted to the WorkManager job)
    private fun getGeofenceTransitionDetails(
        geofenceTransition: Int,
        triggeringGeofences: List<Geofence>,
    ): String {

        // transition type as string (for transmission)
        val geofenceTransitionString: String = getTransitionString(geofenceTransition)

        // get the IDs of each geoFence that was triggered (= SmobShop.id)
        val triggeringGeofencesIdsList = ArrayList<Any>()
        for (geofence in triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.requestId)
        }
        val triggeringGeofencesIdsString: String = TextUtils.join(", ", triggeringGeofencesIdsList)
        return "$geofenceTransitionString: $triggeringGeofencesIdsString"
    }

    private fun getTransitionString(transitionType: Int): String {
        return when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> GeofenceTransitionsWorkService.ENTRY_STRING
            Geofence.GEOFENCE_TRANSITION_EXIT -> GeofenceTransitionsWorkService.EXIT_STRING
            else -> GeofenceTransitionsWorkService.INVALID_STRING
        }
    }

}  // GeofenceBroadcastReceiver