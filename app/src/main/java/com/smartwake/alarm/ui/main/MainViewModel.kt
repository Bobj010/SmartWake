package com.smartwake.alarm.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smartwake.alarm.SmartWakeApp
import com.smartwake.alarm.core.util.DateTimeUtils
import com.smartwake.alarm.data.repository.AlarmRepositoryImpl
import com.smartwake.alarm.domain.model.Alarm
import com.smartwake.alarm.domain.model.Mission
import com.smartwake.alarm.domain.model.MissionType
import com.smartwake.alarm.platform.alarm.AlarmSchedulerImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as SmartWakeApp
    private val repository = AlarmRepositoryImpl(app.database)
    private val scheduler = AlarmSchedulerImpl(application)

    val alarms: StateFlow<List<Alarm>> = repository.getAllAlarms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _nextAlarmInfo = MutableStateFlow<String?>("Будильники отключены")
    val nextAlarmInfo: StateFlow<String?> = _nextAlarmInfo.asStateFlow()

    init {
        viewModelScope.launch {
            alarms.collect { list ->
                updateNextAlarmBanner(list)
            }
        }
    }

    private fun updateNextAlarmBanner(list: List<Alarm>) {
        val active = list.filter { it.isEnabled }
        if (active.isEmpty()) {
            _nextAlarmInfo.value = "Будильники отключены"
            return
        }

        val nextTimestamp = active.minOfOrNull { alarm ->
            DateTimeUtils.calculateNextTriggerMillis(alarm.hour, alarm.minute, alarm.daysOfWeekMask)
        }

        if (nextTimestamp != null) {
            _nextAlarmInfo.value = "Следующий сигнал: " + DateTimeUtils.formatTimeRemaining(nextTimestamp)
        } else {
            _nextAlarmInfo.value = "Будильники отключены"
        }
    }

    fun toggleAlarm(alarm: Alarm, isEnabled: Boolean) {
        viewModelScope.launch {
            val updated = alarm.copy(isEnabled = isEnabled)
            repository.setAlarmEnabled(alarm.id, isEnabled)
            if (isEnabled) {
                scheduler.schedule(updated)
            } else {
                scheduler.cancel(alarm)
            }
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            scheduler.cancel(alarm)
            repository.deleteAlarm(alarm)
        }
    }

    fun createSampleAlarm() {
        viewModelScope.launch {
            val alarmId = UUID.randomUUID().toString()
            val alarm = Alarm(
                id = alarmId,
                hour = 7,
                minute = 0,
                daysOfWeekMask = 31, // По будням
                isEnabled = true,
                label = "Учеба",
                missionType = MissionType.QR_CODE
            )
            val mission = Mission(
                id = UUID.randomUUID().toString(),
                alarmId = alarmId,
                type = MissionType.QR_CODE,
                targetBarcodeHash = "SMARTWAKE_DEMO_CODE",
                barcodeLabel = "Зубная паста в ванной"
            )
            repository.saveAlarm(alarm, mission)
            scheduler.schedule(alarm)
        }
    }
}
