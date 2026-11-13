package com.smartwake.alarm.core.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.smartwake.alarm.R
import com.smartwake.alarm.SmartWakeApp
import com.smartwake.alarm.ui.ringing.AlarmRingActivity

object AlarmNotificationHelper {

    const val NOTIFICATION_ID = 1001

    fun buildAlarmNotification(
        context: Context,
        alarmId: String,
        label: String
    ): Notification {
        val fullScreenIntent = Intent(context, AlarmRingActivity::class.java).apply {
            putExtra("EXTRA_ALARM_ID", alarmId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_NO_USER_ACTION or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            alarmId.hashCode(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, SmartWakeApp.ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("SmartWake: Пора вставать!")
            .setContentText(if (label.isNotBlank()) label else "Будильник звонит")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }
}
