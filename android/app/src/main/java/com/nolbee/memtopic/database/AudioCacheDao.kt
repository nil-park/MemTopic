package com.nolbee.memtopic.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Entity
data class AudioCache(
    @PrimaryKey
    val cacheKey: String,
    val audioBase64: String,
)

@Dao
interface AudioCacheDao {
    @Upsert
    suspend fun upsertCache(cache: AudioCache)

    @Query("SELECT audioBase64 FROM audiocache WHERE cacheKey = :cacheKey LIMIT 1")
    suspend fun getCachedAudio(cacheKey: String): String?

    @Query("SELECT * FROM audiocache WHERE cacheKey IN (:cacheKeys)")
    fun getCachedAudioByKeys(cacheKeys: List<String>): Flow<List<AudioCache>>
}
