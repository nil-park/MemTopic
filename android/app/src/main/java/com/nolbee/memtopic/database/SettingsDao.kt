package com.nolbee.memtopic.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "setting")
data class SettingEntity(
    @PrimaryKey
    val settingKey: String,
    val settingValue: String
)

@Dao
interface SettingsDao {
    @Query("SELECT * FROM setting")
    fun getAllSettings(): Flow<List<SettingEntity>>

    @Upsert
    suspend fun upsertSettings(vararg settings: SettingEntity)

    @Query("DELETE FROM setting WHERE settingKey IN (:keys)")
    suspend fun deleteSettings(vararg keys: String)
}
