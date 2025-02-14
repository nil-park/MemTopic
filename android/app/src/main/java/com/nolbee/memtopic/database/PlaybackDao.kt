package com.nolbee.memtopic.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/*
 * Play audio by splitting the content into individual sentences.
 * Each sentence is played multiple times.
 * Between sentences, there is an interval equal to the duration of the previous sentence.
 */
@Entity
data class Playback(
    @PrimaryKey
    val id: Int = 0,
    val topicId: Int,
    val sentenceIndex: Int,          // The index of the current sentence
    val currentRepetition: Int,      // How many times the current sentence has been played
    val totalRepetitions: Int,       // How many times the current sentence should be repeated
    val isInterval: Boolean = false, // Whether the current section is the interval
    val content: String = "",
)

@Dao
interface PlaybackDao {
    @Upsert
    suspend fun upsertPlayback(state: Playback)

    @Query("SELECT * FROM playback LIMIT 1")
    suspend fun getPlaybackOnce(): Playback?

    @Query("SELECT * FROM playback LIMIT 1")
    fun getPlayback(): Flow<Playback?>
}
