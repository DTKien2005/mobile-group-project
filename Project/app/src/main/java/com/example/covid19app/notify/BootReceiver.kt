package com.example.covid19app.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            Log.d("BootReceiver", "Rescheduling daily reminder after boot")
            NotificationScheduler.rescheduleAfterBoot(context)
        }
    }
}