package com.nolbee.memtopic.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AudioCacheRepository(private val audioCacheDao: AudioCacheDao) {
    fun getIsCachedLines(lines: List<String>): Flow<List<Boolean>> {
        val ttsEngine = "gcp" // TODO: get from Topic
        val languageCode = "en-US" // TODO: get from Topic
        val voiceType = "en-US-Neural2-J" // TODO: get from Topic

        val cacheKeys = lines.map { line ->
            "${ttsEngine}_${languageCode}_${voiceType}_${line.hashCode()}"
        }

        return audioCacheDao.getCachedAudioByKeys(cacheKeys).map { cachedList ->
            val existingKeys = cachedList.map { it.cacheKey }.toSet()
            List(lines.size) { index ->
                cacheKeys[index] in existingKeys
            }
        }
    }
}
