package com.smartwake.alarm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smartwake.alarm.data.local.entity.AlarmLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmLogDao {

    @Query("SELECT * FROM alarm_logs ORDER BY scheduledTimestamp DESC")
    fun getAllLogs(): Flow<List<AlarmLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AlarmLogEntity)
}
