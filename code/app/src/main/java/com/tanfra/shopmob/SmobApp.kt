package com.tanfra.shopmob

import android.app.Application
import android.content.Context
import androidx.work.*
import com.tanfra.shopmob.smob.data.*
import com.tanfra.shopmob.smob.data.local.dbServices
import com.tanfra.shopmob.smob.data.net.netServices
import com.tanfra.shopmob.smob.data.repo.repoServices
import com.tanfra.shopmob.smob.data.repo.*
import com.tanfra.shopmob.smob.data.repo.dataSource.*
import com.tanfra.shopmob.smob.vmServices
import com.tanfra.shopmob.smob.work.RefreshSmobStaticDataWorkerFast
import com.tanfra.shopmob.smob.work.RefreshSmobStaticDataWorkerSlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class SmobApp : Application(), KoinComponent, Configuration.Provider {

    override fun onCreate() {
        super.onCreate()

        // initialize Timber (logging) lib
        Timber.plant(Timber.DebugTree())

        // set context variable
        smobAppContext = applicationContext

        // instantiate viewModels, repos and DBs and inject them as services into consuming classes
        // ... using KOIN framework (as "service locator"): https://insert-koin.io/
        startKoin {

            //androidLogger(Level.DEBUG)

            // inject application context into Koin module
            // ... allows Context to be retrieved/used via 'get()' (see 'myModule', above)
            androidContext(this@SmobApp)

            // declare modules of provided services
            modules(listOf(vmServices, netServices, dbServices, repoServices))

        }

        // initialize WorkManager - running on coroutine scope 'applicationScope'
        delayedInitRecurringWorkSlow()
        delayedInitRecurringWorkFast()

    }  // onCreate


    // share some global variables (simple version)
    companion object {

        // IDs of Workmanager jobs - available for all
        lateinit var repeatingRequestSlowID: UUID
        lateinit var repeatingRequestFastID: UUID

        // application context
        lateinit var smobAppContext: Context

        // add a coroutine scope to be used with WorkManger scheduled work
        val applicationScope = CoroutineScope(Dispatchers.Default)

        // ... schedule some work: slow update cycle
        fun delayedInitRecurringWorkSlow() = applicationScope.launch {
            setupRecurringWorkSlow()
        }

        // ... schedule some work: fast update cycle
        fun delayedInitRecurringWorkFast() = applicationScope.launch {
            setupRecurringWorkFast()
        }

        // ... cancel work: fast update cycle
        fun cancelRecurringWorkFast() {
            WorkManager.getInstance(smobAppContext).cancelWorkById(repeatingRequestFastID)
        }

        // ... cancel work: slow update cycle
        fun cancelRecurringWorkSlow() {
            WorkManager.getInstance(smobAppContext).cancelWorkById(repeatingRequestSlowID)
        }

        // configure the actual work to be scheduled by WorkManager
        private fun setupRecurringWorkSlow() {

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
            val repeatingRequestSlow = PeriodicWorkRequestBuilder<RefreshSmobStaticDataWorkerSlow>(
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(constraintsSlow)
                .setInitialDelay(30, TimeUnit.SECONDS)
                .build()

            // set in companion object, to provide access to this job from all lifecycle callbacks
            repeatingRequestSlowID = repeatingRequestSlow.id


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
            WorkManager.getInstance(smobAppContext).enqueueUniquePeriodicWork(
                RefreshSmobStaticDataWorkerSlow.WORK_NAME_SLOW,
                ExistingPeriodicWorkPolicy.REPLACE,
                repeatingRequestSlow
            )

        }  // setupRecurringWorkSlow


        // configure the actual work to be scheduled by WorkManager
        private fun setupRecurringWorkFast() {

            // work scheduling constraints - fast polling
            val constraintsFast = Constraints.Builder()
                //.setRequiredNetworkType(NetworkType.UNMETERED)
                //.setRequiresCharging(true)
                .build()

            // fast polling task - when app is in foreground
            // ... note: it's fast, as we "sub-schedule" every minute (within "doWork")
            val repeatingRequestFast = PeriodicWorkRequestBuilder<RefreshSmobStaticDataWorkerFast>(
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(constraintsFast)
                .build()

            // set in companion object, to provide access to this job from all lifecycle callbacks
            repeatingRequestFastID = repeatingRequestFast.id

            // fast polling job
            WorkManager.getInstance(smobAppContext).enqueueUniquePeriodicWork(
                RefreshSmobStaticDataWorkerFast.WORK_NAME_FAST,
                ExistingPeriodicWorkPolicy.REPLACE,
                repeatingRequestFast
            )

        }  // setupRecurringWorkFast

    }


    // Initialize WorkManager (needed after WM 2.6, see:
    // https://developer.android.com/topic/libraries/architecture/workmanager/advanced/custom-configuration#on-demand
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }



}