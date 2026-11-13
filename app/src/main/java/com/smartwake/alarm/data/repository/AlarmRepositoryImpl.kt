package com.smartwake.alarm.data.repository

import com.smartwake.alarm.data.local.AppDatabase
import com.smartwake.alarm.data.local.entity.AlarmEntity
import com.smartwake.alarm.data.local.entity.AlarmLogEntity
import com.smartwake.alarm.data.local.entity.MissionEntity
import com.smartwake.alarm.domain.model.Alarm
import com.smartwake.alarm.domain.model.AlarmLog
import com.smartwake.alarm.domain.model.Mission
import com.smartwake.alarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlarmRepositoryImpl(
    private val database: AppDatabase
) : AlarmRepository {

    override fun getAllAlarms(): Flow<List<Alarm>> {
        return database.alarmDao().getAllAlarms().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getActiveAlarms(): List<Alarm> {
        return database.alarmDao().getActiveAlarms().map { it.toDomain() }
    }

    override suspend fun getAlarmById(id: String): Alarm? {
        return database.alarmDao().getAlarmById(id)?.toDomain()
    }

    override suspend fun saveAlarm(alarm: Alarm, mission: Mission?) {
        database.alarmDao().insertAlarm(AlarmEntity.fromDomain(alarm))
        if (mission != null) {
            database.missionDao().insertMission(MissionEntity.fromDomain(mission))
        }
    }

    override suspend fun setAlarmEnabled(id: String, isEnabled: Boolean) {
        database.alarmDao().setAlarmEnabled(id, isEnabled)
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        database.alarmDao().deleteAlarm(AlarmEntity.fromDomain(alarm))
    }

    override suspend fun getMissionForAlarm(alarmId: String): Mission? {
        return database.missionDao().getMissionForAlarm(alarmId)?.toDomain()
    }

    override suspend fun logAlarmEvent(log: AlarmLog) {
        database.alarmLogDao().insertLog(AlarmLogEntity.fromDomain(log))
    }

    override fun getAllLogs(): Flow<List<AlarmLog>> {
        return database.alarmLogDao().getAllLogs().map { list ->
            list.map { it.toDomain() }
        }
    }
}
