package com.smartwake.alarm.platform.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import com.smartwake.alarm.core.notification.AlarmNotificationHelper
import com.smartwake.alarm.core.util.AudioUtils
import com.smartwake.alarm.platform.alarm.AlarmReceiver

class AlarmForegroundService : Service() {

    companion object {
        const val ACTION_STOP_ALARM = "ACTION_STOP_ALARM"
        const val ACTION_SNOOZE_ALARM = "ACTION_SNOOZE_ALARM"
        private var isRunning = false

        fun isAlarmRunning(): Boolean = isRunning
    }

    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var audioUtils: AudioUtils

    override fun onCreate() {
        super.onCreate()
        audioUtils = AudioUtils(this)

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "SmartWake:AlarmForegroundWakeLock"
        ).apply {
            setReferenceCounted(false)
            acquire(10 * 60 * 1000L) // Максимум 10 минут удержания для безопасности
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP_ALARM) {
            stopSelf()
            return START_NOT_STICKY
        }

        val alarmId = intent?.getStringExtra(AlarmReceiver.EXTRA_ALARM_ID) ?: "default"
        val label = intent?.getStringExtra(AlarmReceiver.EXTRA_ALARM_LABEL) ?: ""

        // Показ уведомления и запуск Foreground Service
        val notification = AlarmNotificationHelper.buildAlarmNotification(this, alarmId, label)
        startForeground(AlarmNotificationHelper.NOTIFICATION_ID, notification)

        isRunning = true
        audioUtils.startAlarm(isVibrate = true, isCrescendo = true)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        audioUtils.stopAlarm()
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
