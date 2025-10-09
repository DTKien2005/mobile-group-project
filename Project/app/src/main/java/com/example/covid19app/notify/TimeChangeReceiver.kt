package com.example.covid19app.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.covid19app.notify.NotificationScheduler

class TimeChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Recompute and set the next 9:00 AM when user changes time or timezone
        NotificationScheduler.rescheduleAfterBoot(context)
    }
}