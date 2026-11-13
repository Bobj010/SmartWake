package com.smartwake.alarm.domain.model

data class AlarmLog(
    val id: String,
    val alarmId: String,
    val scheduledTimestamp: Long,
    val dismissedTimestamp: Long,
    val snoozeCountUsed: Int,
    val durationSeconds: Int,
    val wasSuccess: Boolean
)
