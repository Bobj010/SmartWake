package com.smartwake.alarm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.smartwake.alarm.domain.model.AlarmLog

@Entity(
    tableName = "alarm_logs",
    foreignKeys = [
        ForeignKey(
            entity = AlarmEntity::class,
            parentColumns = ["id"],
            childColumns = ["alarmId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["alarmId"])]
)
data class AlarmLogEntity(
    @PrimaryKey
    val id: String,
    val alarmId: String,
    val scheduledTimestamp: Long,
    val dismissedTimestamp: Long,
    val snoozeCountUsed: Int,
    val durationSeconds: Int,
    val wasSuccess: Boolean
) {
    fun toDomain(): AlarmLog {
        return AlarmLog(
            id = id,
            alarmId = alarmId,
            scheduledTimestamp = scheduledTimestamp,
            dismissedTimestamp = dismissedTimestamp,
            snoozeCountUsed = snoozeCountUsed,
            durationSeconds = durationSeconds,
            wasSuccess = wasSuccess
        )
    }

    companion object {
        fun fromDomain(log: AlarmLog): AlarmLogEntity {
            return AlarmLogEntity(
                id = log.id,
                alarmId = log.alarmId,
                scheduledTimestamp = log.scheduledTimestamp,
                dismissedTimestamp = log.dismissedTimestamp,
                snoozeCountUsed = log.snoozeCountUsed,
                durationSeconds = log.durationSeconds,
                wasSuccess = log.wasSuccess
            )
        }
    }
}
