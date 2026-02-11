package com.leolithy.exam_06.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class HydrationReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationHelper.showHydrationNotification(context)
    }
}