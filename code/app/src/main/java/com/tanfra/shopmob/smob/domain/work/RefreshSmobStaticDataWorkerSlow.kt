package com.tanfra.shopmob.smob.domain.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tanfra.shopmob.smob.data.remote.utils.NetworkConnectionManager
import com.tanfra.shopmob.smob.data.repo.`interface`.*
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber


// use WorkManager to do work - derived from CoroutineWorker, as we have async work to be done
// ... need to inherit from KoinComponent to use Koin based DI in this module:
//     see: https://stackoverflow.com/questions/57349196/koin-injecting-into-workmanager
class RefreshSmobStaticDataWorkerSlow(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params), KoinComponent {

    // UUID for our work (to be scheduled by WorkManager)
    companion object {
        const val WORK_NAME_SLOW = "SmobStaticDataWorkerSlow"
    }

    // fetch NetworkConnectionManager form service locator
    private val networkConnectionManager: NetworkConnectionManager by inject()

    // fetch repositories from Koin service locator
    private val smobUserDataSource: SmobUserRepository by inject()
    private val smobGroupDataSource: SmobGroupRepository by inject()
    private val smobProductDataSource: SmobProductRepository by inject()
    private val smobShopDataSource: SmobShopRepository by inject()
    private val smobListDataSource: SmobListRepository by inject()

    // define work to be done
    override suspend fun doWork(): Result {

        return try {

            // fetch smob data from backend - also initializes LiveData _statusSmobDataSync to
            // LOADING
            // ... received data is used to update the DB
            Timber.i("Running scheduled work ($WORK_NAME_SLOW) ---------------------------")

            // only sync - if the network is up
            if (networkConnectionManager.isNetworkConnected) {

                // update users in local DB from backend DB
                smobUserDataSource.refreshDataInLocalDB()
                smobGroupDataSource.refreshDataInLocalDB()
                smobProductDataSource.refreshDataInLocalDB()
                smobShopDataSource.refreshDataInLocalDB()
                smobListDataSource.refreshDataInLocalDB()

            }

            // return 'success' - done
            Timber.i("Scheduled work ($WORK_NAME_SLOW) completed successfully")
            Result.success()

        } catch (e: ResponseException) {

            // return 'failure' - retry
            Timber.i("Scheduled work ($WORK_NAME_SLOW) could not be run - retrying")
            Timber.i("Exception details: ${e.response.status.value}: ${e.response.bodyAsText()}")
            Result.retry()

        }

    }  // doWork()

}