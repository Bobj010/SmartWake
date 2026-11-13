package com.smartwake.alarm.platform.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.smartwake.alarm.SmartWakeApp
import com.smartwake.alarm.data.repository.AlarmRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            val app = context.applicationContext as? SmartWakeApp ?: return
            val repository = AlarmRepositoryImpl(app.database)
            val scheduler = AlarmSchedulerImpl(context)

            CoroutineScope(Dispatchers.IO).launch {
                val activeAlarms = repository.getActiveAlarms()
                activeAlarms.forEach { alarm ->
                    scheduler.schedule(alarm)
                }
            }
        }
    }
}
