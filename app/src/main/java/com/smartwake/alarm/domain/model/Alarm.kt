package com.smartwake.alarm.domain.model

enum class MissionType {
    NONE,
    QR_CODE,
    MATH,
    SHAKE,
    MEMORY
}

data class Alarm(
    val id: String,
    val hour: Int,
    val minute: Int,
    val daysOfWeekMask: Int = 0,
    val isEnabled: Boolean = true,
    val label: String = "",
    val isVibrate: Boolean = true,
    val isCrescendo: Boolean = true,
    val volume: Int = 100,
    val snoozeLimit: Int = 2,
    val missionType: MissionType = MissionType.QR_CODE
)
