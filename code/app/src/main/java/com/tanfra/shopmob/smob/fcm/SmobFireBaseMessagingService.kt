package com.tanfra.shopmob.smob.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tanfra.shopmob.smob.work.SmobAppWork
import com.tanfra.shopmob.utils.sendNotificationOnFcm
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class SmobFireBaseMessagingService : FirebaseMessagingService(), KoinComponent {

    // fetch worker class form service locator - includes application context
    private val wManager: SmobAppWork by inject()

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Timber.i("From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        remoteMessage.data.let {
            Timber.i("Message data payload: " + remoteMessage.data)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Timber.i("Message Notification Body: ${it.body}")
            sendNotificationOnFcm(wManager.smobAppContext, it.body!!)
        }
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
    }

}