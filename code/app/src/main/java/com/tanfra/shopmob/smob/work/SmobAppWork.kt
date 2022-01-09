package com.tanfra.shopmob.smob.work

import android.content.Context
import androidx.work.*
import com.tanfra.shopmob.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SmobAppWork(context: Context): Configuration.Provider {

    // add a coroutine scope to be used with WorkManger scheduled work
    val applicationScope = CoroutineScope(Dispatchers.Default)

    // convenience: store handle to application context
    var smobAppContext: Context

    init {

        // context for all...
        smobAppContext = context

        // provide custom configuration
        // ... see: https://developer.android.com/topic/libraries/architecture/workmanager/advanced/custom-configuration#custom
        val myConfig = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

        // initialize WM (necessary, as we took out the 'standard initialization' via Manifest)
        WorkManager.initialize(smobAppContext, myConfig)

    }


    // Initialize WorkManager (needed after WM 2.6, see:
    // https://developer.android.com/topic/libraries/architecture/workmanager/advanced/custom-configuration#on-demand
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }

    // ... check if some job (by TAG) has already been cancelled
    private fun isWorkCancelled(tag: String): Boolean = WorkManager.getInstance(smobAppContext).getWorkInfosByTag(tag).isCancelled


    // ... schedule some work: slow update cycle
    fun delayedInitRecurringWorkSlow() = applicationScope.launch {

        // setup the slow polling job and start it
        scheduleRecurringWorkSlow(setupRecurringWorkSlow())

    }

    // ... schedule some work: fast update cycle
    fun delayedInitRecurringWorkFast() = applicationScope.launch {

        // setup the fast polling job and start it
        scheduleRecurringWorkFast(setupRecurringWorkFast())

    }

    // ... cancel work: fast update cycle
    fun cancelRecurringWorkFast() = applicationScope.launch {
        if(!isWorkCancelled(RefreshSmobStaticDataWorkerFast.WORK_NAME_FAST)) {
            Timber.i("Stopping fast polling work job...")
            WorkManager.getInstance(smobAppContext).cancelAllWorkByTag(RefreshSmobStaticDataWorkerFast.WORK_NAME_FAST)
        }
    }

    // ... cancel work: slow update cycle
    fun cancelRecurringWorkSlow() = applicationScope.launch {
        if(!isWorkCancelled(RefreshSmobStaticDataWorkerSlow.WORK_NAME_SLOW)) {
            Timber.i("Stopping slow polling work job...")
            WorkManager.getInstance(smobAppContext).cancelAllWorkByTag(RefreshSmobStaticDataWorkerSlow.WORK_NAME_SLOW)
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
                ExistingPeriodicWorkPolicy.REPLACE,
                request
            )
    }


    // configure the actual work to be scheduled by WorkManager
    fun setupRecurringWorkFast(): OneTimeWorkRequest {

        // work scheduling constraints - fast polling
        val constraintsFast = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        // fast polling task - when app is in foreground
        // ... note: it's fast, as we "sub-schedule" every minute (within "doWork")
        return OneTimeWorkRequestBuilder<RefreshSmobStaticDataWorkerFast>()
            .setInitialDelay(Constants.WORK_POLLING_FAST_VALUE, TimeUnit.SECONDS)
            .setConstraints(constraintsFast)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS)
            .addTag(RefreshSmobStaticDataWorkerFast.WORK_NAME_FAST)
            .build()

    }  // setupRecurringWorkFast

    // ... now schedule the slow polling job
    fun scheduleRecurringWorkFast(request: OneTimeWorkRequest) = applicationScope.launch {

        // fast polling job
        Timber.i("Starting fast polling work job...")
        WorkManager.getInstance(smobAppContext).enqueueUniqueWork(
                RefreshSmobStaticDataWorkerFast.WORK_NAME_FAST,
                ExistingWorkPolicy.REPLACE,
                request
            )

    }

}