/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tanfra.shopmob.smob.fcm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tanfra.shopmob.R
import com.tanfra.shopmob.utils.notificationManager
import com.tanfra.shopmob.utils.sendNotificationOnFcmUpdate

class FcmUpdateReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // update prompt received (FCM) --> notify application via associated channel
        context.notificationManager?.sendNotificationOnFcmUpdate(
            context,
            context.getText(R.string.smob_list_name).toString(),
        )
    }

}