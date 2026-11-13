package com.smartwake.alarm.platform.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(
    context: Context,
    private val targetShakes: Int = 40,
    private val onProgress: (current: Int, target: Int) -> Unit,
    private val onComplete: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var shakeCount = 0
    private var lastShakeTimestamp = 0L
    private val shakeThreshold = 13.0f // Ускорение m/s^2

    fun start() {
        shakeCount = 0
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat() - SensorManager.GRAVITY_EARTH

        val now = System.currentTimeMillis()
        if (acceleration > shakeThreshold && (now - lastShakeTimestamp) > 250) {
            lastShakeTimestamp = now
            shakeCount++
            onProgress(shakeCount, targetShakes)

            if (shakeCount >= targetShakes) {
                stop()
                onComplete()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
