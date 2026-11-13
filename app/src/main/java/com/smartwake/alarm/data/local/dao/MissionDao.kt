package com.smartwake.alarm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smartwake.alarm.data.local.entity.MissionEntity

@Dao
interface MissionDao {

    @Query("SELECT * FROM missions WHERE alarmId = :alarmId LIMIT 1")
    suspend fun getMissionForAlarm(alarmId: String): MissionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: MissionEntity)

    @Update
    suspend fun updateMission(mission: MissionEntity)

    @Query("DELETE FROM missions WHERE alarmId = :alarmId")
    suspend fun deleteMissionForAlarm(alarmId: String)
}
