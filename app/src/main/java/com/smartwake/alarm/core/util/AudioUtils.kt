package com.smartwake.alarm.core.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioUtils(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var crescendoJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    /**
     * Запускает проигрывание будильника с нарастанием громкости (Crescendo)
     */
    fun startAlarm(isVibrate: Boolean = true, isCrescendo: Boolean = true) {
        stopAlarm()

        try {
            val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, alertUri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                if (isCrescendo) {
                    setVolume(0.05f, 0.05f)
                } else {
                    setVolume(1.0f, 1.0f)
                }
                prepare()
                start()
            }

            if (isCrescendo) {
                startCrescendo()
            }

            if (isVibrate) {
                startVibrate()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startCrescendo() {
        crescendoJob = scope.launch {
            var currentVolume = 0.05f
            while (isActive && currentVolume < 1.0f) {
                delay(1500) // Нарастание за ~30 секунд
                currentVolume = (currentVolume + 0.05f).coerceAtMost(1.0f)
                mediaPlayer?.setVolume(currentVolume, currentVolume)
            }
        }
    }

    private fun startVibrate() {
        val pattern = longArrayOf(0, 800, 400, 800, 400, 1000)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(pattern, 0)
            vibrator?.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    }

    /**
     * Останавливает звук и вибрацию
     */
    fun stopAlarm() {
        crescendoJob?.cancel()
        crescendoJob = null

        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            vibrator?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Короткий виброотклик подтверждения (например, правильный скан QR)
     */
    fun playSuccessFeedback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(150)
        }
    }

    /**
     * Двойной виброотклик ошибки (не тот код)
     */
    fun playErrorFeedback() {
        val pattern = longArrayOf(0, 100, 80, 100)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, -1)
        }
    }
}
