package com.tanfra.shopmob

import android.app.Application
import androidx.work.*
import com.tanfra.shopmob.smob.data.*
import com.tanfra.shopmob.smob.data.local.LocalDB
import com.tanfra.shopmob.smob.data.repo.*
import com.tanfra.shopmob.smob.data.repo.dataSource.*
import com.tanfra.shopmob.smob.smoblist.SmobItemListViewModel
import com.tanfra.shopmob.smob.saveitem.SaveSmobItemViewModel
import com.tanfra.shopmob.smob.work.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SmobApp : Application(), Configuration.Provider {

    // add a coroutine scope to be used with WorkManger scheduled work
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        // initialize Timber (logging) lib
        Timber.plant(Timber.DebugTree())

        // KOIN module - used as service provider throughout the app
        val myModule = module {

            // declare a ViewModel - to be injected into Fragment with dedicated injector using
            // "by viewModel()"
            //
            // class SmobItemListViewModel(
            //    app: Application,
            //    private val dataSource: SmobItemDataSource
            // ) : BaseViewModel(app) { ... }
            viewModel {
                SmobItemListViewModel(
                    get(),  // app (context)
                    get() as SmobItemDataSource  // repo as data source
                )
            }

            // declare a ViewModel - to be injected into Fragment with standard injector using
            // "by inject()"
            // --> this view model is declared singleton to be used across multiple fragments
            //
            // class SaveSmobItemViewModel(
            //    val app: Application,
            //    val dataSource: SmobItemDataSource
            // ) : BaseViewModel(app) { ... }
            single {
                SaveSmobItemViewModel(
                    get(),
                    get() as SmobItemDataSource
                )
            }

            // Room DB ----------------------------------------------------------------

            // local DB singleton "Room" object representing smob database
            // ... used as (local) data source for all repositories of the app
            single { LocalDB.createSmobDatabase(this@SmobApp) }

            // DAOs -------------------------------------------------------------------

            // DAO to access table smobItems in the above DB (smob.db)
            single { LocalDB.createSmobItemDao(get()) }

            // DAO to access table smobUsers in the above DB (smob.db)
            single { LocalDB.createSmobUserDao(get()) }

            // DAO to access table smobGroups in the above DB (smob.db)
            single { LocalDB.createSmobGroupDao(get()) }

            // DAO to access table smobShops in the above DB (smob.db)
            single { LocalDB.createSmobShopDao(get()) }

            // DAO to access table smobProducts in the above DB (smob.db)
            single { LocalDB.createSmobProductDao(get()) }

            // DataSources ------------------------------------------------------------

            // declare a (singleton) repository service with interface "SmobItemDataSource"
            single<SmobItemDataSource> { SmobItemRepository(get()) }

            // declare a (singleton) repository service with interface "SmobUserDataSource"
            single<SmobUserDataSource> { SmobUserRepository(get()) }

            // declare a (singleton) repository service with interface "SmobGroupDataSource"
            single<SmobGroupDataSource> { SmobGroupRepository(get()) }

            // declare a (singleton) repository service with interface "SmobShopDataSource"
            single<SmobShopDataSource> { SmobShopRepository(get()) }

            // declare a (singleton) repository service with interface "SmobProductDataSource"
            single<SmobProductDataSource> { SmobProductRepository(get()) }
        }

        // instantiate viewModels, repos and DBs and inject them as services into consuming classes
        // ... using KOIN framework (as "service locator"): https://insert-koin.io/
        startKoin {
            // inject application context into Koin module
            // ... allows Context to be retrieved/used via 'get()' (see 'myModule', above)
            androidContext(this@SmobApp)
            modules(listOf(myModule))
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
        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(
            1,
            TimeUnit.DAYS
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
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )

    }  // setupRecurringWork

}