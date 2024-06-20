package com.nolbee.memtopic.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Entity
data class Topic(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val lastModified: Date,
    val lastPlayback: Date,
    val content: String
)

@Dao
interface TopicDao {
    @Upsert
    suspend fun upsertTopic(topic: Topic)

    @Delete
    suspend fun deleteTopic(topic: Topic)

    @Query("SELECT * FROM topic ORDER BY title ASC")
    fun selectTopicByName(): Flow<List<Topic>>

    @Query("SELECT * FROM topic ORDER BY lastModified DESC")
    fun selectTopicByLastModified(): Flow<List<Topic>>

    @Query("SELECT * FROM topic ORDER BY lastPlayback DESC")
    fun selectTopicByLastPlayback(): Flow<List<Topic>>
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

@Database(entities = [Topic::class], version = 1)
@TypeConverters(Converters::class)
abstract class TopicDatabase : RoomDatabase() {
    abstract val dao: TopicDao
}
