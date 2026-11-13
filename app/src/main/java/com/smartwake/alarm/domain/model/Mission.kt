package com.smartwake.alarm.domain.model

enum class MathDifficulty {
    EASY,
    MEDIUM,
    HARD
}

data class Mission(
    val id: String,
    val alarmId: String,
    val type: MissionType,
    val targetBarcodeHash: String = "",
    val barcodeLabel: String = "Ванная комната",
    val mathDifficulty: MathDifficulty = MathDifficulty.MEDIUM,
    val mathQuestionsCount: Int = 3,
    val shakeTargetCount: Int = 40,
    val memoryGridSize: Int = 3,
    val emergencyPin: String = "123456"
)
