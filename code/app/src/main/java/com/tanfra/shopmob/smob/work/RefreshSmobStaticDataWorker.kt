package com.tanfra.shopmob.smob.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tanfra.shopmob.smob.data.repo.dataSource.SmobUserDataSource
import com.tanfra.shopmob.smob.data.repo.SmobUserRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import retrofit2.HttpException
import timber.log.Timber

// use WorkManager to do work - derived from CoroutineWorker, as we have async work to be done
class RefreshSmobStaticDataWorker(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {

    // UUID for our work (to be scheduled by WorkManager)
    companion object {
        const val WORK_NAME = "SmobStaticDataWorker"
    }

    // define work to be done
    override suspend fun doWork(): Result {

        return try {

            // fetch smob data from backend - also initializes LiveData _statusSmobDataSync to
            // LOADING
            // ... received data is used to update the DB
            Timber.i("Running scheduled work (refreshSmobStaticDataInDB)")

            // fetch user data repro from Koin service locator
            val smobUserRepository: SmobUserDataSource by inject(SmobUserDataSource::class.java)
            smobUserRepository.refreshSmobUserDataInDB()
            
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