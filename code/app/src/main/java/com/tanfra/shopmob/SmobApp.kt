package com.tanfra.shopmob

import android.app.Application
import com.tanfra.shopmob.smob.work.wmServices
import com.tanfra.shopmob.smob.ui.vmServices
import com.tanfra.shopmob.smob.data.local.dbServices
import com.tanfra.shopmob.smob.data.net.netServices
import com.tanfra.shopmob.smob.data.repo.repoServices
import androidx.work.*
import com.tanfra.shopmob.smob.data.*
import com.tanfra.shopmob.smob.data.repo.*
import com.tanfra.shopmob.smob.data.repo.dataSource.*
import com.tanfra.shopmob.smob.work.SmobAppWork
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import timber.log.Timber
import java.util.*



class SmobApp : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        // initialize Timber (logging) lib
        Timber.plant(Timber.DebugTree())

        // instantiate viewModels, repos and DBs and inject them as services into consuming classes
        // ... using KOIN framework (as "service locator"): https://insert-koin.io/
        startKoin {

            //androidLogger(Level.DEBUG)

            // inject application context into Koin module
            // ... allows Context to be retrieved/used via 'get()' (see 'myModule', above)
            androidContext(this@SmobApp)

            // declare modules of provided services
            modules(listOf(wmServices, vmServices, netServices, dbServices, repoServices))

        }

//        // fetch worker class form service locator
//        val wManager: SmobAppWork by inject()
//
//        // initialize WorkManager jobs (slow and fast polling)... and start them both
//        wManager.delayedInitRecurringWorkSlow()
//        wManager.delayedInitRecurringWorkFast()

    }  // onCreate


    // this will only run on the emulator (not on any production devices)
    override fun onTerminate() {
        super.onTerminate()

//        // fetch worker class form service locator
//        val wManager: SmobAppWork by inject()
//
//        // initialize WorkManager jobs (slow and fast polling)... and start them both
//        wManager.cancelRecurringWorkFast()
//        wManager.cancelRecurringWorkSlow()

    }

}