package com.smartwake.alarm.domain.repository

import com.smartwake.alarm.domain.model.Alarm
import com.smartwake.alarm.domain.model.AlarmLog
import com.smartwake.alarm.domain.model.Mission
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun getAllAlarms(): Flow<List<Alarm>>
    suspend fun getActiveAlarms(): List<Alarm>
    suspend fun getAlarmById(id: String): Alarm?
    suspend fun saveAlarm(alarm: Alarm, mission: Mission?)
    suspend fun setAlarmEnabled(id: String, isEnabled: Boolean)
    suspend fun deleteAlarm(alarm: Alarm)
    suspend fun getMissionForAlarm(alarmId: String): Mission?
    suspend fun logAlarmEvent(log: AlarmLog)
    fun getAllLogs(): Flow<List<AlarmLog>>
}
