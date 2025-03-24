package com.nolbee.memtopic.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert

@Entity(tableName = "setting")
data class SettingEntity(
    @PrimaryKey
    val settingKey: String,
    val settingValue: String
)

@Dao
interface SettingsDao {
    @Query("SELECT * FROM setting WHERE settingKey = :key LIMIT 1")
    suspend fun getSetting(key: String): SettingEntity?

    @Query("SELECT * FROM setting")
    suspend fun getAllSettings(): List<SettingEntity>

    @Upsert
    suspend fun upsertSettings(vararg settings: SettingEntity)

    @Query("DELETE FROM setting WHERE settingKey = :key")
    suspend fun deleteSetting(key: String)
}
