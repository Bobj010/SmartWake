package com.smartwake.alarm.ui.ringing

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.smartwake.alarm.core.designsystem.SmartWakeTheme
import com.smartwake.alarm.platform.service.AlarmForegroundService

class AlarmRingActivity : ComponentActivity() {

    private val viewModel: AlarmRingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Разбудить экран и показать поверх блокировки (Android 8.0 - 14+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        val alarmId = intent.getStringExtra("EXTRA_ALARM_ID") ?: "default"
        viewModel.initAlarm(alarmId)

        setContent {
            SmartWakeTheme {
                AlarmRingScreen(
                    viewModel = viewModel,
                    onDismissAlarm = {
                        stopAlarmService()
                        finishAndRemoveTask()
                    }
                )
            }
        }
    }

    private fun stopAlarmService() {
        val stopIntent = Intent(this, AlarmForegroundService::class.java).apply {
            action = AlarmForegroundService.ACTION_STOP_ALARM
        }
        startService(stopIntent)
    }

    // Запрет глушения звука кнопками громкости на торце
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_MUTE -> true // Поглощаем нажатие
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onBackPressed() {
        // Запрещаем закрытие будильника кнопкой «Назад»
    }
}
