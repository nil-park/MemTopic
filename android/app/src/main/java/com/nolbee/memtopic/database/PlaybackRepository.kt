package com.nolbee.memtopic.database

import kotlinx.coroutines.flow.Flow

class PlaybackRepository(private val playbackDao: PlaybackDao) {
    fun getPlayback(): Flow<Playback?> {
        return playbackDao.getPlayback()
    }

    suspend fun getPlaybackOnce(): Playback? {
        return playbackDao.getPlaybackOnce()
    }

    suspend fun setCurrentLine(index: Int) {
        val playback = playbackDao.getPlaybackOnce()
        if (playback != null) {
            playbackDao.upsertPlayback(
                playback.copy(sentenceIndex = index, currentRepetition = 0)
            )
        }
    }
}
