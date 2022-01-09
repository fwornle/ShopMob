package com.tanfra.shopmob.smob.work

import android.content.Context
import androidx.work.*
import com.tanfra.shopmob.Constants
import com.tanfra.shopmob.smob.data.repo.dataSource.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.HttpException
import timber.log.Timber
import java.util.concurrent.TimeUnit

// use WorkManager to do work - derived from CoroutineWorker, as we have async work to be done
// ... need to inherit from KoinComponent to use Koin based DI in this module:
//     see: https://stackoverflow.com/questions/57349196/koin-injecting-into-workmanager
class RefreshSmobStaticDataWorkerFast(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params), KoinComponent {

    // UUID for our work (to be scheduled by WorkManager)
    companion object {
        const val WORK_NAME_FAST = "SmobStaticDataWorkerFast"
    }

    // fetch worker class form service locator
    private val wManager: SmobAppWork by inject()

    // fetch repositories from Koin service locator
    private val smobUserDataSource: SmobUserDataSource by inject()
    private val smobGroupDataSource: SmobGroupDataSource by inject()
    private val smobProductDataSource: SmobProductDataSource by inject()
    private val smobShopDataSource: SmobShopDataSource by inject()
    private val smobListDataSource: SmobListDataSource by inject()

    // define work to be done
    override suspend fun doWork(): Result {

        // rescheduling
        val nextRequest = wManager.setupRecurringWorkFast()

        return try {

            // fetch smob data from backend
            // ... received data is used to update the DB
            Timber.i("Running scheduled work ($WORK_NAME_FAST) ---------------------------")
            Timber.i("Work ID:  ${getId()}")

            // update users in local DB from backend DB
            smobUserDataSource.refreshDataInLocalDB()
            smobGroupDataSource.refreshDataInLocalDB()
            smobProductDataSource.refreshDataInLocalDB()
            smobShopDataSource.refreshDataInLocalDB()
            smobListDataSource.refreshDataInLocalDB()

            // re-schedule
            wManager.scheduleRecurringWorkFast(nextRequest)

            // return 'success' - done
            Timber.i("Scheduled work ($WORK_NAME_FAST) completed successfully")
            Timber.i("Work ID:  ${nextRequest.getId()}")
            Result.success()

        } catch (e: HttpException) {

            // return 'failure' - retry
            Timber.i("Scheduled work ($WORK_NAME_FAST) could not be run - retrying")
            Result.retry()

        }

    }  // doWork()

}