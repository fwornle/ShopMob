package com.tanfra.shopmob.smob.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tanfra.shopmob.smob.data.repo.dataSource.*
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.HttpException
import timber.log.Timber

// use WorkManager to do work - derived from CoroutineWorker, as we have async work to be done
// ... neeed to inherit from KoinComponent to use Koin based DI in this module:
//     see: https://stackoverflow.com/questions/57349196/koin-injecting-into-workmanager
class RefreshSmobStaticDataWorker(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params), KoinComponent {

    // UUID for our work (to be scheduled by WorkManager)
    companion object {
        const val WORK_NAME = "SmobStaticDataWorker"
    }

    // fetch repositories from Koin service locator
    private val smobUserDataSource: SmobUserDataSource by inject()
    private val smobGroupDataSource: SmobGroupDataSource by inject()
    private val smobProductDataSource: SmobProductDataSource by inject()
    private val smobShopDataSource: SmobShopDataSource by inject()
    private val smobListDataSource: SmobListDataSource by inject()

    // define work to be done
    override suspend fun doWork(): Result {

        return try {

            // fetch smob data from backend - also initializes LiveData _statusSmobDataSync to
            // LOADING
            // ... received data is used to update the DB
            Timber.i("Running scheduled work (refreshSmobStaticDataInDB)")

            // WORKAROUND (for missing backend - no update notifications yet --> polling)
            // ... run every minute
            //     note: WorkManager (re)-schedules this work job every 15 minutes
            //           --> need to "sub-divide" this into 15 further
            for(idx in 1 .. 15) {

                // update users in local DB from backend DB
                Timber.i("Refreshing data in local DB ($idx/15)")
                smobUserDataSource.refreshDataInLocalDB()
                smobGroupDataSource.refreshDataInLocalDB()
                smobProductDataSource.refreshDataInLocalDB()
                smobShopDataSource.refreshDataInLocalDB()
                smobListDataSource.refreshDataInLocalDB()

                // suspend for 60 seconds
                delay(60000)

            }

            // return 'success' - done
            Timber.i("Scheduled work (refreshAsteroidsInDB) run successfully")
            Result.success()

        } catch (e: HttpException) {

            // return 'failure' - retry
            Timber.i("Scheduled work (refreshAsteroidsInDB) could not be run - retrying")
            Result.retry()

        }

    }  // doWork()

}