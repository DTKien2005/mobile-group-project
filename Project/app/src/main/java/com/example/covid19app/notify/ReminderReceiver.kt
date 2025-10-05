package com.example.covid19app.notify

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.covid19app.notify.NotificationScheduler
import com.example.covid19app.R
import com.example.covid19app.VnDashboardActivity

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        // Tap -> open the dashboard
        val openIntent = Intent(context, VnDashboardActivity::class.java)
        val contentPending = PendingIntent.getActivity(
            context,
            2001,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // NEW: detect if this is the 60s test alarm
        val isTest = intent?.getBooleanExtra("is_test", false) == true

        // Build the notification (label changes if it's a test)
        val notification = NotificationCompat.Builder(context, NotificationScheduler.NOTIF_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) // replace with your bell icon if you have one
            .setContentTitle(if (isTest) "Test notification" else "Daily update")
            .setContentText(if (isTest) "This is your 60s test alarm" else "Your 9:00 AM notification ✅")
            .setAutoCancel(true)
            .setContentIntent(contentPending)
            .build()

        val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mgr.notify(9001, notification)

        // Schedule the next day at the same time
        NotificationScheduler.scheduleDailyReminder(context)

    }
}