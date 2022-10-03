package com.tanfra.shopmob

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.tanfra.shopmob.Constants.FCM_TOPIC
import com.tanfra.shopmob.smob.data.local.RefreshLocalDB
import com.tanfra.shopmob.smob.data.local.dbServices
import com.tanfra.shopmob.smob.data.net.netServices
import com.tanfra.shopmob.smob.data.net.utils.NetworkConnectionManager
import com.tanfra.shopmob.smob.data.repo.ato.SmobUserATO
import com.tanfra.shopmob.smob.data.repo.repoServices
import com.tanfra.shopmob.smob.ui.vmServices
import com.tanfra.shopmob.smob.work.SmobAppWork
import com.tanfra.shopmob.smob.work.wmServices
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import timber.log.Timber


class SmobApp : Application(), KoinComponent {

    companion object{
        // details of currently logged-in user
        var currUser: SmobUserATO? = null

    }

    override fun onCreate() {
        super.onCreate()

        // initialize Timber (logging) lib
        Timber.plant(Timber.DebugTree())

        // start background polling timer
        RefreshLocalDB.timer.start()

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

        // fetch worker class form service locator
        val wManager: SmobAppWork by inject()

        // initialize WorkManager job (slow polling)... and start it
        wManager.delayedInitRecurringWorkSlow()

        // subscribe to topic for FCM update messages
        subscribeTopic(wManager.smobAppContext)

        // start monitoring the network connection
        val networkConnectionManager: NetworkConnectionManager by inject()
        networkConnectionManager.startListenNetworkState()

    }  // onCreate


    // this will only run on the emulator (not on any production devices)
    override fun onTerminate() {
        super.onTerminate()

        // fetch worker class form service locator
        val wManager: SmobAppWork by inject()

        // cancel WorkManager job (slow polling)
        wManager.cancelRecurringWorkSlow()

        // unsubscribe from topic used for FCM update messages
        FirebaseMessaging.getInstance().unsubscribeFromTopic(FCM_TOPIC)

        // stop monitoring the network connection
        val networkConnectionManager: NetworkConnectionManager by inject()
        networkConnectionManager.stopListenNetworkState()

    }

    // Firebase Messaging Service -------------------------------------------------
    private val fcm by lazy { FirebaseMessaging.getInstance() }

    // debug - ensure that FCM SDK is properly started
    private fun logRegistrationToken() {
        fcm.token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Timber.w("Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                Timber.i(msg)
                // Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
        )
    }

    // FCM: subscribe to topic
    private fun subscribeTopic(context: Context) {

        // log the Firebase SDK registration toke to the console (debug only)
        logRegistrationToken()

        // subscribe to topic (for update messaging)
        fcm.subscribeToTopic(FCM_TOPIC)
            .addOnCompleteListener { task ->
                var message = getString(R.string.message_subscribed)
                if (!task.isSuccessful) {
                    message = getString(R.string.message_subscribe_failed)
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

    }

}