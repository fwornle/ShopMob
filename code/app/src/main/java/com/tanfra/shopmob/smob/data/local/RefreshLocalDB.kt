package com.tanfra.shopmob.smob.data.local

import android.os.CountDownTimer
import com.tanfra.shopmob.Constants
import com.tanfra.shopmob.Constants.WORK_POLLING_FAST_VALUE
import com.tanfra.shopmob.smob.data.repo.dataSource.*
import com.tanfra.shopmob.smob.work.SmobAppWork
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

/**
 * Singleton class that is used to refresh local database
 */
object RefreshLocalDB: KoinComponent {

    // refresh timer (polling of backend DB)
    val timer by lazy {

        // ticks = duration = (Constants.)WORK_POLLING_FAST_VALUE
        // --> only trigger 'onTick' once (at the end)
        object: CountDownTimer(WORK_POLLING_FAST_VALUE, WORK_POLLING_FAST_VALUE) {
            override fun onTick(millisUntilFinished: Long) { /* do nothing on 'tick' */ }
            override fun onFinish() {
                refreshSmobDb() // doWork
                this.start()    // restart to make recurrent
            }
        }

    }

    // fetch worker class form service locator
    private val wManager: SmobAppWork by inject()

    // fetch repositories from Koin service locator
    private val smobUserDataSource: SmobUserDataSource by inject()
    private val smobGroupDataSource: SmobGroupDataSource by inject()
    private val smobProductDataSource: SmobProductDataSource by inject()
    private val smobShopDataSource: SmobShopDataSource by inject()
    private val smobListDataSource: SmobListDataSource by inject()

    // refresh (polling)
    private fun refreshSmobDb() {

        // fetch smob data from backend
        // ... received data is used to update the DB
        Timber.i("Refreshing local DB from backend DB ---------------------------------- ")

        // avoid series of timeouts, if network has already been found to be inactive
        // ... see: ResponseHandler
        if(wManager.netActive) {

            // launch polling of all backend tables (parallel coroutines)
            wManager.applicationScope.launch {

                // update users in local DB from backend DB
                smobUserDataSource.refreshDataInLocalDB()
                smobGroupDataSource.refreshDataInLocalDB()
                smobProductDataSource.refreshDataInLocalDB()
                smobShopDataSource.refreshDataInLocalDB()
                smobListDataSource.refreshDataInLocalDB()

            }  // coroutine scope

        }  // netActive

        // return 'success' - done
        Timber.i("Refresh completed successfully")

    }  // refreshSmobDb

}