package com.nolbee.memtopic.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

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
    abstract fun topicDao(): TopicDao
}
