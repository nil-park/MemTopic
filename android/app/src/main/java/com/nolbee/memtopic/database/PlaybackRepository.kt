package com.nolbee.memtopic.database

import kotlinx.coroutines.flow.Flow

class PlaybackRepository(private val playbackDao: PlaybackDao) {
    suspend fun upsertPlayback(state: Playback) {
        playbackDao.upsertPlayback(state)
    }

    suspend fun getPlaybackOnce(): Playback? {
        return playbackDao.getPlaybackOnce()
    }

    fun getPlayback(): Flow<Playback?> {
        return playbackDao.getPlayback()
    }
}
