package com.tanfra.shopmob

import android.app.Application
import androidx.work.*
import com.tanfra.shopmob.smob.data.*
import com.tanfra.shopmob.smob.data.local.dto.dbModule
import com.tanfra.shopmob.smob.data.net.netModule
import com.tanfra.shopmob.smob.data.repo.*
import com.tanfra.shopmob.smob.data.repo.dataSource.*
import com.tanfra.shopmob.smob.viewModelModule
import com.tanfra.shopmob.smob.work.RefreshSmobStaticDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SmobApp : Application(), Configuration.Provider {

    // add a coroutine scope to be used with WorkManger scheduled work
    val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        // initialize Timber (logging) lib
        Timber.plant(Timber.DebugTree())

        // instantiate viewModels, repos and DBs and inject them as services into consuming classes
        // ... using KOIN framework (as "service locator"): https://insert-koin.io/
        startKoin {

            // inject application context into Koin module
            // ... allows Context to be retrieved/used via 'get()' (see 'myModule', above)
            androidContext(this@SmobApp)

            // declare modules
            modules(listOf(viewModelModule, netModule, dbModule))

            // set-up as per: https://medium.com/@harmittaa/setting-up-koin-2-0-1-for-android-ebf11de01816
            AndroidLogger()


        }

        // initialize WorkManager - running on coroutine scope 'applicationScope'
        delayedInit()

    }  // onCreate


    // Initialize WorkManager (needed after WM 2.6, see:
    // https://developer.android.com/topic/libraries/architecture/workmanager/advanced/custom-configuration#on-demand
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }


    // configure WorkManager to schedule some recurring work (daily update of asteroids DB)
    private fun delayedInit() = applicationScope.launch {
        setupRecurringWork()
    }

    // configure the actual work to be scheduled by WorkManager
    private fun setupRecurringWork() {

        // define some constraints und which the repeating request should be scheduled:
        // WIFI, charging
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .build()

        // define configuration of WorkManager job: scheduling frequency, constraints (see above)
        // ... this is for the background updates of 'quasi static' user data, shop data, etc.
        //     --> slow (twice a day)
        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshSmobStaticDataWorker>(
            12,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        // register 'repeating request' with WorkManager for the specified 'work job'
        //
        // !!!!!!!!!!!!!!!!!!! NOTE:
        //
        // ... use 'ExistingPeriodicWorkPolicy.REPLACE' (at least for one run of the app) to set
        //     the constraints and the frequency of the work to be scheduled (otherwise it remains
        //     stuck on whatever was requested the first time the worker was registered - after
        //     [deleting + re-]installing the app)
        // ... subsequently (= after one run), the policy can be (/ should be?) changed to KEEP
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RefreshSmobStaticDataWorker.WORK_NAME,
            //ExistingPeriodicWorkPolicy.KEEP,
            ExistingPeriodicWorkPolicy.REPLACE,
            repeatingRequest
        )

    }  // setupRecurringWork

}