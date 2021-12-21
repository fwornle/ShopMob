package com.tanfra.shopmob

import android.app.Application
import com.tanfra.shopmob.smob.data.SmobItemDataSource
import com.tanfra.shopmob.smob.data.local.LocalDB
import com.tanfra.shopmob.smob.smoblist.SmobItemListViewModel
import com.tanfra.shopmob.smob.saveitem.SaveSmobItemViewModel
import com.tanfra.shopmop.smob.data.local.SmobItemsLocalRepository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

class SmobApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // initialize Timber (logging) lib
        Timber.plant(Timber.DebugTree())

        /**
         * use Koin Library as a service locator
         */
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

            // declare a (singleton) repository service with interface "SmobItemDataSource"
            // note: the repo needs the DAO of the room database (SmobItemsDao)
            //       ... which is why it is declared (below) as singleton object and injected
            //           using "get()" in the lambda of the declaration
            //
            // class SmobItemsLocalRepository(
            //    private val smobItemsDao: SmobItemsDao,
            //    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
            //) : SmobItemDataSource { ... }
            single<SmobItemDataSource> { SmobItemsLocalRepository(get()) }

            // declare the local DB singleton object - used as data source for the repository
            // note: LocalDB.createSmobItemDao returns a DAO with interface SmobItemsDao
            //       ... the DAO is needed by the repo (where it is injected, see "get()", above)
            //
            // object LocalDB {
            //
            //    /**
            //     * static method that creates a smob item class and returns the DAO of the
            //     * smob item
            //     */
            //    fun createSmobItemDao(context: Context): SmobItemsDao {
            //        return Room.databaseBuilder(
            //            context.applicationContext,
            //            SmobItemsDatabase::class.java, "smobItems.db"
            //        ).build().smobItemsDao()
            //    }
            single { LocalDB.createSmobItemDao(this@SmobApp) }
        }

        // instantiate viewModels, repos and DBs and inject them as services into consuming classes
        // ... using KOIN framework (as "service locator"): https://insert-koin.io/
        startKoin {
            androidContext(this@SmobApp)
            modules(listOf(myModule))
        }
    }
}