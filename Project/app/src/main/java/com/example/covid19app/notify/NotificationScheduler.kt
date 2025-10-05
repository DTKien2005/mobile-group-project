package com.example.covid19app.notify

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import java.util.Calendar
import androidx.core.net.toUri

object NotificationScheduler {

    private const val CHANNEL_ID = "daily_reminder_channel"
    private const val CHANNEL_NAME = "Daily Reminder"
    internal const val NOTIF_CHANNEL_ID = CHANNEL_ID

    /** Returns true if we can use setExactAndAllowWhileIdle without SecurityException. */
    fun canUseExactAlarms(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val am = context.getSystemService(AlarmManager::class.java)
        return am.canScheduleExactAlarms()
    }

    /**
     * If exact-alarms permission is missing on API 31+, opens the system screen so the
     * user can allow it. Returns true if we *requested* it (caller should wait & retry),
     * false if not needed.
     */
    fun requestExactAlarmIfNeeded(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !canUseExactAlarms(context)) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = "package:${context.packageName}".toUri()
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            Toast.makeText(
                context,
                "Please allow “Alarms & reminders” to schedule the 9:00 AM reminder.",
                Toast.LENGTH_LONG
            ).show()
            return true
        }
        return false
    }

    fun scheduleDailyReminder(context: Context, hour: Int = 9, minute: Int = 0) {
        createNotificationChannel(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cal = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }

        try {
            // Exact alarm (requires user consent on API 31+)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                pending
            )
        } catch (se: SecurityException) {
            // Fallback: inexact alarm + user notice
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pending)
            Toast.makeText(
                context,
                "Exact alarms not allowed. Scheduled a flexible reminder instead.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun rescheduleAfterBoot(context: Context) {
        scheduleDailyReminder(context) // default 9:00 AM
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Daily 9:00 AM reminders" }
            mgr.createNotificationChannel(channel)
        }
    }

    fun scheduleTestIn(context: Context, secondsFromNow: Int = 60) {
        createNotificationChannel(context)

        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("is_test", true)
        }
        val pending = PendingIntent.getBroadcast(
            context, 2002, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerAt = System.currentTimeMillis() + secondsFromNow * 1000L

        try {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
        } catch (_: SecurityException) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
        }
    }
}