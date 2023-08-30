package com.nolbee.memtopic.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {
    @Upsert
    suspend fun upsertTopic(topic: Topic)

    @Delete
    suspend fun deleteTopic(topic: Topic)

    @Query("SELECT * FROM topic ORDER BY name ASC")
    fun selectTopicByName(): Flow<List<Topic>>

    @Query("SELECT * FROM topic ORDER BY lastModified DESC")
    fun selectTopicByLastModified(): Flow<List<Topic>>

    @Query("SELECT * FROM topic ORDER BY lastPlayback DESC")
    fun selectTopicByLastPlayback(): Flow<List<Topic>>
}
