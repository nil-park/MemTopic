package com.nolbee.memtopic.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 새로 추가한 테이블 생성 (예: settings 테이블)
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `setting` (" +
                    "`settingKey` TEXT NOT NULL, " +
                    "`settingValue` TEXT NOT NULL, " +
                    "PRIMARY KEY(`settingKey`))"
        )
    }
}
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            ALTER TABLE Topic 
            ADD COLUMN `options` TEXT NOT NULL 
            DEFAULT '{"languageCode":"en-US","voiceType":"en-US-Neural2-J"}'
            """
        ) // TODO: default languageCode, voiceType from R.string
    }
}
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS Playback")
        db.execSQL(
            """
            CREATE TABLE Playback (
                id INTEGER NOT NULL,
                topicId INTEGER NOT NULL,
                sentenceIndex INTEGER NOT NULL,
                currentRepetition INTEGER NOT NULL,
                totalRepetitions INTEGER NOT NULL,
                isPlaying INTEGER NOT NULL,
                content TEXT NOT NULL,
                PRIMARY KEY(id)
            )
            """
        )
    }
}
