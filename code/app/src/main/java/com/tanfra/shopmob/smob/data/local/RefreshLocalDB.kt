package com.tanfra.shopmob.smob.data.local

import android.os.CountDownTimer
import com.tanfra.shopmob.Constants.WORK_POLLING_FAST_VALUE
import com.tanfra.shopmob.SmobApp
import com.tanfra.shopmob.smob.data.remote.utils.NetworkConnectionManager
import com.tanfra.shopmob.smob.data.repo.`interface`.*
import com.tanfra.shopmob.smob.domain.work.SmobAppWork
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
                // this mechanism should only be active in 'polling mode'
                // (= fallback, if FCM 'push notification mode' unavailable)
                if(SmobApp.backendPollingActive) {
                    refreshSmobDb() // doWork
                    this.start()    // restart to make recurrent
                }
            }  // onFinish
        }  // CountDownTimer

    }

    // fetch NetworkConnectionManager and SmobAppWork form service locator
    private val networkConnectionManager: NetworkConnectionManager by inject()
    private val wManager: SmobAppWork by inject()

    // fetch repositories from Koin service locator
    private val smobUserDataSource: SmobUserRepository by inject()
    private val smobGroupDataSource: SmobGroupRepository by inject()
    private val smobProductDataSource: SmobProductRepository by inject()
    private val smobShopDataSource: SmobShopRepository by inject()
    private val smobListDataSource: SmobListRepository by inject()

    // refresh (polling)
    private fun refreshSmobDb() {

        // fetch smob data from backend
        // ... received data is used to update the DB
        Timber.i("Refreshing local DB from backend DB ---------------------------------- ")

        // avoid series of timeouts, if network has already been found to be inactive
        // ... see: ResponseHandler
        if(networkConnectionManager.isNetworkConnected) {

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
        Timber.i("Refresh triggered successfully")

    }  // refreshSmobDb

}