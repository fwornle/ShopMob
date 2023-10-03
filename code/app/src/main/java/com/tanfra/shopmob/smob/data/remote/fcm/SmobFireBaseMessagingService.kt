package com.tanfra.shopmob.smob.data.remote.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tanfra.shopmob.app.SmobApp
import com.tanfra.shopmob.smob.data.remote.utils.NetworkConnectionManager
import com.tanfra.shopmob.smob.data.repo.repoIf.*
import com.tanfra.shopmob.smob.domain.work.SmobAppWork
import com.tanfra.shopmob.app.utils.sendNotificationOnFcmUpdate
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class SmobFireBaseMessagingService : FirebaseMessagingService(), KoinComponent {

    // fetch worker class form service locator - includes application context
    private val wManager: SmobAppWork by inject()

    // fetch NetworkConnectionManager form service locator
    private val networkConnectionManager: NetworkConnectionManager by inject()

    // fetch repositories from Koin service locator
    private val smobUserDataSource: SmobUserRepository by inject()
    private val smobGroupDataSource: SmobGroupRepository by inject()
    private val smobProductDataSource: SmobProductRepository by inject()
    private val smobShopDataSource: SmobShopRepository by inject()
    private val smobListDataSource: SmobListRepository by inject()

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Timber.i("From: ${remoteMessage.from}")

        // only process message if we're (still) connected to the network
        if (networkConnectionManager.isNetworkConnected) {

            // sanity check: FCM message should (always) contain a payload
            if (remoteMessage.data.isNotEmpty()) {

                // update local DB according to changed element
                remoteMessage.data.let { payload ->

                    val element = payload.getValue("element")
                    val table = payload.getValue("table")

                    // update local DB according to FCM message
                    Timber.i("FCM: ${SmobApp.currUser?.username}: refreshing element '${element}' in your local table '${table}'")

                    // suspended call --> launch co-routine
                    wManager.applicationScope.launch {

                        when (table) {
                            "users" -> smobUserDataSource.refreshSmobItemInLocalDB(element)
                            "groups" -> smobGroupDataSource.refreshSmobItemInLocalDB(element)
                            "lists" -> smobListDataSource.refreshSmobItemInLocalDB(element)
                            "products" -> smobProductDataSource.refreshSmobItemInLocalDB(element)
                            "shops" -> smobShopDataSource.refreshSmobItemInLocalDB(element)
                            else -> Timber.i("FCM: received unknown message type ($payload)")
                        }

                    }  // co-routine

                }  // payload w/h data

                // send notification (only happens when app is in the background, i.e. when the change
                // was initiated by another user on another device)
                remoteMessage.notification?.let { msg ->
                    Timber.i("FCM message notification body: ${msg.body}")
                    msg.body?.let { body ->
                        sendNotificationOnFcmUpdate(
                            wManager.smobAppContext,
                            body
                        )
                    }
                }

            }  // payload found

        }  // networkConnectivity

    }
    // [END receive_message]


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Timber.i("Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]


    /**
     * Persist token to third-party servers.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Timber.i("TODO: (not yet implemented:) sendRegistrationTokenToServer($token)")
    }

}