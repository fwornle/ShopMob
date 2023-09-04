package com.tanfra.shopmob.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import com.tanfra.shopmob.BuildConfig
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.ui.details.SmobDetailsActivity
import com.tanfra.shopmob.smob.ui.zeUiBase.NavigationSource
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO
import com.tanfra.shopmob.smob.ui.planning.SmobPlanningActivity

// use app dependent, but otherwise fixed ID for notification channels
private const val NOTIFICATION_GEOFENCE_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel.geofence"
private const val NOTIFICATION_FCM_UPDATE_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel.fcmUpdate"

// generate unique ID for PendingIntent(s) and notification channel(s)
private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())

// extend class Context by a property 'notificationManager' (read only --> only getter defined)
val Context.notificationManager: NotificationManager?
    get() = getSystemService()


/**
 * Create and show a simple notification indicating which Smobshop triggered the geofence.
 *
 * @param context Application context of ShopMob.
 * @param daShop Details of Smobshop that triggered the geofence
 */
fun sendNotificationOnGeofenceHit(context: Context, daShop: SmobShopATO) {
    context.notificationManager?.sendNotificationOnGeofenceHit(context, daShop)
}

/**
 * Create and show a simple notification containing the received FCM message.
 *
 * @param context Application context of ShopMob.
 * @param messageBody FCM message body received.
 */
fun sendNotificationOnFcmUpdate(context: Context, messageBody: String) {
    context.notificationManager?.sendNotificationOnFcmUpdate(context, messageBody)
}


/**
 * Builds and delivers the notification upon the hitting of an active geofence.
 *
 * @param context, activity context.
 * @param daShop, SmobShop details.
 */
fun NotificationManager.sendNotificationOnGeofenceHit(context: Context, daShop: SmobShopATO) {

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

        // channel already created?
        if (getNotificationChannel(NOTIFICATION_GEOFENCE_CHANNEL_ID) == null) {
            // nope --> create notification channel for (the buffering of) geofence hits
            NotificationChannel(
                NOTIFICATION_GEOFENCE_CHANNEL_ID,
                context.getString(R.string.app_name) + ".geofence",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                createNotificationChannel(this)
            }
        }

    }  // OREO and above


    // create intent to start activity SmobDetailsActivity (via 'newIntent' factory)
    val gotoDaShopDetailsIntent = SmobDetailsActivity.newIntent(
        context.applicationContext,
        NavigationSource.GEOFENCE,
        daShop
    )

    // create a pending intent that opens SmobDetailsActivity when user clicks on notification
    val stackBuilder = TaskStackBuilder.create(context)
        .addParentStack(SmobDetailsActivity::class.java)
        .addNextIntent(gotoDaShopDetailsIntent)
    val notificationGeofenceHitPendingIntent =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            stackBuilder
                .getPendingIntent(
                    getUniqueId(),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
        } else {
            stackBuilder
                .getPendingIntent(
                    getUniqueId(),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
        }

    // prettify notifications (with the SmobShop logo)
    val smobLogo = BitmapFactory.decodeResource(
        context.applicationContext.resources,
        R.drawable.smob_2,
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(smobLogo)

    // add action intent to directly go to "Planning" view
    val planningViewIntent = Intent(context, SmobPlanningActivity::class.java)
    val planningViewPendingIntent: PendingIntent =
        PendingIntent.getBroadcast(
            context,
            getUniqueId(),
            planningViewIntent,
            PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )

    // build the notification object for geofence hit notifications, with the data to be shown
    val notification = NotificationCompat.Builder(context, NOTIFICATION_GEOFENCE_CHANNEL_ID)
        .setSmallIcon(R.drawable.smob_2)
        .setContentTitle(daShop.name)
        .setContentText(daShop.description)
        .setContentIntent(notificationGeofenceHitPendingIntent)
        .setStyle(bigPicStyle)
        .setLargeIcon(smobLogo)
        .addAction(
            R.drawable.smob_2,
            context.getString(R.string.smob_lists_name),
            planningViewPendingIntent
        )
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    // deliver the notification
    notify(getUniqueId(), notification)

}


/**
 * Builds and delivers the notification upon the receipt of an FCM update message.
 *
 * @param context, activity context.
 * @param messageBody, notification text.
 */
fun NotificationManager.sendNotificationOnFcmUpdate(context: Context, messageBody: String) {

    // initialize notification channel for FCM update messages
    ifSupportsOreo {
        // channel already created?
        if (getNotificationChannel(NOTIFICATION_FCM_UPDATE_CHANNEL_ID) == null) {
            // nope --> create notification channel for (the buffering of) FCM update messages
            NotificationChannel(
                NOTIFICATION_FCM_UPDATE_CHANNEL_ID,
                context.getString(R.string.app_name) + ".fcmUpdate",
                NotificationManager.IMPORTANCE_DEFAULT
            )
                .apply {
                    createNotificationChannel(this)
                }
        }

    }  // OREO and above

    // create intent to go to SmobPlanning
    val smobPlanningIntent = Intent(context, SmobPlanningActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        context,
        getUniqueId(),
        smobPlanningIntent,
        PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE,
    )

    // prettify notifications (with the SmobShop logo)
    val smobLogo = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.smob_2,
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(smobLogo)

    // build the notification
    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.smob_update_notification_channel_id)
    )
        .setSmallIcon(R.drawable.smob_2)
        .setContentTitle(context.getString(R.string.smob_list_name))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setStyle(bigPicStyle)
        .setLargeIcon(smobLogo)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    // deliver the notification
    notify(getUniqueId(), builder.build())

}


