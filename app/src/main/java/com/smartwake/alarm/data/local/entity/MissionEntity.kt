package com.smartwake.alarm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.smartwake.alarm.domain.model.MathDifficulty
import com.smartwake.alarm.domain.model.Mission
import com.smartwake.alarm.domain.model.MissionType

@Entity(
    tableName = "missions",
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
data class MissionEntity(
    @PrimaryKey
    val id: String,
    val alarmId: String,
    val type: String,
    val targetBarcodeHash: String,
    val barcodeLabel: String,
    val mathDifficulty: String,
    val mathQuestionsCount: Int,
    val shakeTargetCount: Int,
    val memoryGridSize: Int,
    val emergencyPin: String
) {
    fun toDomain(): Mission {
        return Mission(
            id = id,
            alarmId = alarmId,
            type = try { MissionType.valueOf(type) } catch (e: Exception) { MissionType.QR_CODE },
            targetBarcodeHash = targetBarcodeHash,
            barcodeLabel = barcodeLabel,
            mathDifficulty = try { MathDifficulty.valueOf(mathDifficulty) } catch (e: Exception) { MathDifficulty.MEDIUM },
            mathQuestionsCount = mathQuestionsCount,
            shakeTargetCount = shakeTargetCount,
            memoryGridSize = memoryGridSize,
            emergencyPin = emergencyPin
        )
    }

    companion object {
        fun fromDomain(mission: Mission): MissionEntity {
            return MissionEntity(
                id = mission.id,
                alarmId = mission.alarmId,
                type = mission.type.name,
                targetBarcodeHash = mission.targetBarcodeHash,
                barcodeLabel = mission.barcodeLabel,
                mathDifficulty = mission.mathDifficulty.name,
                mathQuestionsCount = mission.mathQuestionsCount,
                shakeTargetCount = mission.shakeTargetCount,
                memoryGridSize = mission.memoryGridSize,
                emergencyPin = mission.emergencyPin
            )
        }
    }
}
