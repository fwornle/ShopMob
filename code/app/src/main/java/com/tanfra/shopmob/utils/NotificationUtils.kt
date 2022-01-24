package com.tanfra.shopmob.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import com.tanfra.shopmob.BuildConfig
import com.tanfra.shopmob.R
import com.tanfra.shopmob.smob.data.repo.ato.SmobShopATO

private const val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel"

// append notification manager as service to Context
val Context.notificationManager: NotificationManager?
    get() = getSystemService<NotificationManager>()

fun sendNotification(context: Context, daShop: SmobShopATO) {

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

}

private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())