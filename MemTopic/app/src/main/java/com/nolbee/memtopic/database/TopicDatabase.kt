package com.nolbee.memtopic.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Topic::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class TopicDatabase : RoomDatabase() {
    abstract val dao: TopicDao
}
