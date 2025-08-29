package com.nolbee.memtopic.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    version = 6
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
    fun provideDatabase(
        @ApplicationContext context: Context
    ): TopicDatabase = Room.databaseBuilder(
        context,
        TopicDatabase::class.java,
        "topicDatabase"
    )
        .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
        .build()

    @Provides
    fun provideTopicDao(db: TopicDatabase) = db.topicDao()

    @Provides
    fun providePlaybackDao(db: TopicDatabase) = db.playbackDao()

    @Provides
    fun provideAudioCacheDao(db: TopicDatabase) = db.audioCacheDao()

    @Provides
    fun provideSettingsDao(db: TopicDatabase) = db.settingsDao()

    @Provides
    @Singleton
    fun provideTopicRepository(
        topicDao: TopicDao
    ) = TopicRepository(topicDao)

    @Provides
    @Singleton
    fun providePlaybackRepository(
        playbackDao: PlaybackDao
    ) = PlaybackRepository(playbackDao)

    @Provides
    @Singleton
    fun provideAudioCacheRepository(
        @ApplicationContext context: Context,
        audioCacheDao: AudioCacheDao
    ) = AudioCacheRepository(context, audioCacheDao)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        settingsDao: SettingsDao
    ) = SettingsRepository(settingsDao)
}
