package com.nolbee.memtopic.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.Date
import javax.inject.Singleton

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

@Database(
    entities = [
        Topic::class,
        Playback::class,
        AudioCache::class,
        SettingEntity::class,
    ],
    version = 4
)
@TypeConverters(
    Converters::class
)
abstract class TopicDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
    abstract fun playbackDao(): PlaybackDao
    abstract fun audioCacheDao(): AudioCacheDao
    abstract fun settingsDao(): SettingsDao
}

@Module
@InstallIn(SingletonComponent::class)
object TopicDatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application): TopicDatabase {
        return Room.databaseBuilder(app, TopicDatabase::class.java, "topicDatabase")
            .addMigrations(MIGRATION_3_4)
            .build()
    }

    @Provides
    fun provideTopicDao(db: TopicDatabase): TopicDao {
        return db.topicDao()
    }

    @Provides
    fun providePlaybackDao(db: TopicDatabase): PlaybackDao {
        return db.playbackDao()
    }

    @Provides
    fun provideAudioCacheDao(db: TopicDatabase): AudioCacheDao {
        return db.audioCacheDao()
    }

    @Provides
    fun provideSettingsDao(db: TopicDatabase): SettingsDao {
        return db.settingsDao()
    }

    @Provides
    fun provideTopicRepository(topicDao: TopicDao): TopicRepository {
        return TopicRepository(topicDao)
    }

    @Provides
    fun providePlaybackRepository(playbackDao: PlaybackDao): PlaybackRepository {
        return PlaybackRepository(playbackDao)
    }

    @Provides
    fun provideAudioCacheRepository(audioCacheDao: AudioCacheDao): AudioCacheRepository {
        return AudioCacheRepository(audioCacheDao)
    }

    @Provides
    fun provideSettingsRepository(settingsDao: SettingsDao): SettingsRepository {
        return SettingsRepository(settingsDao)
    }
}
