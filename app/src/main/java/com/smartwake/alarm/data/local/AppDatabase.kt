package com.smartwake.alarm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smartwake.alarm.data.local.dao.AlarmDao
import com.smartwake.alarm.data.local.dao.AlarmLogDao
import com.smartwake.alarm.data.local.dao.MissionDao
import com.smartwake.alarm.data.local.entity.AlarmEntity
import com.smartwake.alarm.data.local.entity.AlarmLogEntity
import com.smartwake.alarm.data.local.entity.MissionEntity

@Database(
    entities = [
        AlarmEntity::class,
        MissionEntity::class,
        AlarmLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao
    abstract fun missionDao(): MissionDao
    abstract fun alarmLogDao(): AlarmLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smartwake.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
