package com.tanfra.shopmob.smob.domain.work

import android.content.Context
import androidx.work.*
import com.tanfra.shopmob.smob.domain.geofence.GeofenceTransitionsWorkService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

@Suppress("SameParameterValue")
class SmobAppWork(context: Context) {

    // add a coroutine scope to be used with WorkManger scheduled work
    val applicationScope = CoroutineScope(Dispatchers.Default)

    // convenience: store handle to application context
    val smobAppContext = context


    // ... check if some job (by TAG) has already been cancelled
    private fun isWorkCancelled(tag: String): Boolean =
        WorkManager
            .getInstance(smobAppContext)
            .getWorkInfosByTag(tag)
            .isCancelled


    // ... schedule some work: slow update cycle
    fun delayedInitRecurringWorkSlow() = applicationScope.launch {

        // setup the slow polling job and start it
        scheduleRecurringWorkSlow(setupRecurringWorkSlow())

    }

    // ... cancel work: slow update cycle
    fun cancelRecurringWorkSlow() = applicationScope.launch {
        if(!isWorkCancelled(RefreshSmobStaticDataWorkerSlow.WORK_NAME_SLOW)) {
            Timber.i("Stopping slow polling work job...")
            WorkManager.getInstance(smobAppContext).cancelAllWorkByTag(
                RefreshSmobStaticDataWorkerSlow.WORK_NAME_SLOW
            )
        }
    }

    // configure the actual work to be scheduled by WorkManager
    private fun setupRecurringWorkSlow(): PeriodicWorkRequest {

        // define some constraints und which the repeating request should be scheduled:
        // WIFI, charging
        //
        // NOTE:
        // ... backend not ready, currently no notifications of changes
        //     --> use background work to poll DB every minute
        //     --> METERED & no charging
        //
        // work scheduling constraints - slow polling
        val constraintsSlow = Constraints.Builder()
            //.setRequiredNetworkType(NetworkType.UNMETERED)
            //.setRequiresCharging(true)
            .build()


        // define configuration of WorkManager job: scheduling frequency, constraints (see above)
        // ... this is for the background updates of 'quasi static' user data, shop data, etc.
        //     --> slow (every hour - if conditions are met [UNMETERED])
        // NOTE:
        // ... backend not ready, currently no notifications of changes
        //     --> use background work to poll DB every 15 minutes
        //     --> within this 15 minute block (run on a coroutine), take 'sub-steps' with delay()
        //
        // slow polling task - when app is in background
        return PeriodicWorkRequestBuilder<RefreshSmobStaticDataWorkerSlow>(
            15,
            TimeUnit.MINUTES
        )
            .setConstraints(constraintsSlow)
            .setInitialDelay(30, TimeUnit.SECONDS)
            .addTag(RefreshSmobStaticDataWorkerSlow.WORK_NAME_SLOW)
            .build()

    }  // setupRecurringWorkSlow

    // ... now schedule the slow polling job
    private fun scheduleRecurringWorkSlow(request: PeriodicWorkRequest) = applicationScope.launch {

        // register 'repeating request' with WorkManager for the specified 'work job'
        //
        // !!!!!!!!!!!!!!!!!!! NOTE:
        //
        // ... use 'ExistingPeriodicWorkPolicy.REPLACE' (at least for one run of the app) to set
        //     the constraints and the frequency of the work to be scheduled (otherwise it remains
        //     stuck on whatever was requested the first time the worker was registered - after
        //     [deleting + re-]installing the app)
        // ... subsequently (= after one run), the policy can be (/ should be?) changed to KEEP
        //
        // slow polling job
        Timber.i("Starting slow polling work job...")
        WorkManager.getInstance(smobAppContext).enqueueUniquePeriodicWork(
            RefreshSmobStaticDataWorkerSlow.WORK_NAME_SLOW,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
    }

    // configure the geoFence notification work (job) to be scheduled by WorkManager
    fun setupOnTimeJobForGeoFenceNotification(param: String): OneTimeWorkRequest {

        // build data bundle to be communicated to background task
        // ... includes everything that is needed in the display of the notification
        //     (using JSON encoded string to transport SmobShop details, eg. name, category, ...)
        val data = Data.Builder()
            .putString(GeofenceTransitionsWorkService.GEOFENCE_EVENT_PARAM, param)
            .build()

        // geoFence task
        return OneTimeWorkRequestBuilder<GeofenceTransitionsWorkService>()
            .apply { setInputData(data) }
            .addTag(GeofenceTransitionsWorkService.GEOFENCE_WORK_NAME)
            .build()

    }  // setupOnTimeJobForGeoFenceNotification

    // schedule the geoFence notification background job
    // ... as one-time unique job, triggered when the user clicks on the notification the installed
    //     onReceive handler of the BroadcastReceiver has sent (onReceive triggered by geoFence)
    fun scheduleUniqueWorkForGeoFenceNotification(request: OneTimeWorkRequest) = applicationScope.launch {

        // geoFence job
        Timber.i("Scheduling geoFence notification work job...")
        WorkManager.getInstance(smobAppContext).enqueueUniqueWork(
            GeofenceTransitionsWorkService.GEOFENCE_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )

    }

}