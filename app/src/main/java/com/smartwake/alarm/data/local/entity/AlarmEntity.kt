package com.smartwake.alarm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.smartwake.alarm.domain.model.Alarm
import com.smartwake.alarm.domain.model.MissionType

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey
    val id: String,
    val hour: Int,
    val minute: Int,
    val daysOfWeekMask: Int,
    val isEnabled: Boolean,
    val label: String,
    val isVibrate: Boolean,
    val isCrescendo: Boolean,
    val volume: Int,
    val snoozeLimit: Int,
    val missionType: String
) {
    fun toDomain(): Alarm {
        return Alarm(
            id = id,
            hour = hour,
            minute = minute,
            daysOfWeekMask = daysOfWeekMask,
            isEnabled = isEnabled,
            label = label,
            isVibrate = isVibrate,
            isCrescendo = isCrescendo,
            volume = volume,
            snoozeLimit = snoozeLimit,
            missionType = try {
                MissionType.valueOf(missionType)
            } catch (e: Exception) {
                MissionType.QR_CODE
            }
        )
    }

    companion object {
        fun fromDomain(alarm: Alarm): AlarmEntity {
            return AlarmEntity(
                id = alarm.id,
                hour = alarm.hour,
                minute = alarm.minute,
                daysOfWeekMask = alarm.daysOfWeekMask,
                isEnabled = alarm.isEnabled,
                label = alarm.label,
                isVibrate = alarm.isVibrate,
                isCrescendo = alarm.isCrescendo,
                volume = alarm.volume,
                snoozeLimit = alarm.snoozeLimit,
                missionType = alarm.missionType.name
            )
        }
    }
}
