package com.tanfra.shopmob.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import com.tanfra.shopmob.BuildConfig
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.SmobItemDescriptionActivity
import com.tanfra.shopmob.smob.types.SmobItem

private const val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel"

// Android version check for "O" (Oreo)
fun ifSupportsOreo(f: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        f()
    }
}

// append notification manager as service to Context
val Context.notificationManager: NotificationManager?
    get() = getSystemService<NotificationManager>()

fun sendNotification(context: Context, shopMobDataItem: SmobItem) {

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



    // We need to create a NotificationChannel associated with our CHANNEL_ID before sending a notification.
    ifSupportsOreo {
        if (context.notificationManager?.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null
        ) {
            val name = context.getString(R.string.app_name)
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            context.notificationManager?.createNotificationChannel(channel)
        }
    }

    // create intent which starts activity SmobItemDescriptionActivity, with extra data
    // 'SmobItem'
    val intent = SmobItemDescriptionActivity.newIntent(context.applicationContext, shopMobDataItem)

    // create a pending intent that opens SmobItemDescriptionActivity when the user clicks on the notification
    val stackBuilder = TaskStackBuilder.create(context)
        .addParentStack(SmobItemDescriptionActivity::class.java)
        .addNextIntent(intent)
    val notificationPendingIntent = stackBuilder
        .getPendingIntent(getUniqueId(), PendingIntent.FLAG_UPDATE_CURRENT)

    // build the notification object with the data to be shown
    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(shopMobDataItem.title)
        .setContentText(shopMobDataItem.location)
        .setContentIntent(notificationPendingIntent)
        .setAutoCancel(true)
        //.setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    context.notificationManager?.notify(getUniqueId(), notification)
}

private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())