package com.tanfra.shopmob.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import com.tanfra.shopmob.BuildConfig
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.details.SmobDetailsActivity
import com.tanfra.shopmob.smob.ui.details.SmobDetailsSources
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.ui.planning.SmobPlanningActivity

// fixed ID for notification channels
private const val NOTIFICATION_GEOFENCE_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel.geofence"
private const val NOTIFICATION_FCM_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel.fcm"

// unique ID for PendingIntent(s) and notification channel(s)
private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())

// append notification manager as service to Context
val Context.notificationManager: NotificationManager?
    get() = getSystemService<NotificationManager>()


// send notifications to SmobShop upon receiving a geofence hit (= we are near a SmobShop)
fun sendNotificationOnGeofenceHit(context: Context, daShop: SmobShopATO) {

    // old way...
    //
    //    val notificationManager = context
    //        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    //
    // improved: using KTX 'extension functions' (to attach NotificationManager to 'context')
    // val notificationManager = context.getSystemService<NotificationManager>()
    //
    // even better (and that's used here now): append the NotificationManager service to a newly
    // defined Context property 'notificationManager' (see above defined KTX 'extension property')


    // create notification channel for geofence hits (= whenever we are near a SmobShop)
    ifSupportsOreo {

        // first time --> create notification channel for (the buffering of) geofence hits
        if (
            context.notificationManager
                ?.getNotificationChannel(NOTIFICATION_GEOFENCE_CHANNEL_ID) == null
        ) {
            val name = context.getString(R.string.app_name) + ".geofence"
            val channel = NotificationChannel(
                NOTIFICATION_GEOFENCE_CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            context.notificationManager?.createNotificationChannel(channel)
        }

    }  // OREO and above


    // create intent to start activity SmobDetailsActivity
    val intent = SmobDetailsActivity.newIntent(
        context.applicationContext,
        SmobDetailsSources.GEOFENCE,
        daShop
    )

    // create a pending intent that opens SmobDetailsActivity when the user clicks on the notification
    val stackBuilder = TaskStackBuilder.create(context)
        .addParentStack(SmobDetailsActivity::class.java)
        .addNextIntent(intent)
    val notificationGeofenceHitPendingIntent = stackBuilder
        .getPendingIntent(getUniqueId(), PendingIntent.FLAG_UPDATE_CURRENT)

    // prettify notifications (with the SmobShop logo)
    val smobLogo = BitmapFactory.decodeResource(
        context.applicationContext.resources,
        R.drawable.smob_1,
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(smobLogo)
        .bigLargeIcon(null)

    // build the notification object for geofence hit notifications, with the data to be shown
    val notification = NotificationCompat.Builder(context, NOTIFICATION_GEOFENCE_CHANNEL_ID)
        .setSmallIcon(R.drawable.smob_1)
        .setContentTitle(daShop.name)
        .setContentText(daShop.description)
        .setContentIntent(notificationGeofenceHitPendingIntent)
        .setStyle(bigPicStyle)
        .setLargeIcon(smobLogo)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    // deliver the notification
    context.notificationManager?.notify(getUniqueId(), notification)
}



/**
 * Create and show a simple notification containing the received FCM message.
 *
 * @param messageBody FCM message body received.
 */
fun sendNotificationOnFcm(context: Context, messageBody: String) {
    context.notificationManager?.sendNotification(messageBody, context)
}


/**
 * Builds and delivers the notification upon the receipt of an FCM message.
 *
 * @param messageBody, notification text.
 * @param context, activity context.
 */
fun NotificationManager.sendNotification(messageBody: String, context: Context) {

    val contentIntent = Intent(context, SmobPlanningActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        context,
        getUniqueId(),
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // prettify notifications (with the SmobShop logo)
    val smobLogo = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.smob_2,
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(smobLogo)
        .bigLargeIcon(null)

    // build the notification
    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.smob_list_desc)
    )
        .setSmallIcon(R.drawable.smob_2)
        .setContentTitle(context.getString(R.string.smob_list_name))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setStyle(bigPicStyle)
        .setLargeIcon(smobLogo)
//        .addAction(
//            R.drawable.smob_2,
//            applicationContext.getString(R.string.snooze),
//            snoozePendingIntent
//        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    // deliver the notification
    notify(getUniqueId(), builder.build())

}

/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}
