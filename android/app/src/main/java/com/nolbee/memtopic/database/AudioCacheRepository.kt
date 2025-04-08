package com.nolbee.memtopic.database

import android.util.Log
import com.nolbee.memtopic.client.TextToSpeechGCP
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject

class AudioCacheRepository(private val audioCacheDao: AudioCacheDao) {
    fun getIsCachedLines(lines: List<String>, voiceOptions: String): Flow<List<Boolean>> {
        val jsonObject = JSONObject(voiceOptions)
        val languageCode = jsonObject.optString("languageCode", "en-US")
        val voiceType = jsonObject.optString("voiceType", "en-US-Neural2-J")
        Log.d(
            "AudioCacheRepository",
            "voiceOptions: $voiceOptions, languageCode: $languageCode, voiceType: $voiceType"
        )

        val cacheKeys = lines.map { line ->
            TextToSpeechGCP.makeCacheKey(languageCode, voiceType, line)
        }

        return audioCacheDao.getCachedAudioByKeys(cacheKeys).map { cachedList ->
            val existingKeys = cachedList.map { it.cacheKey }.toSet()
            List(lines.size) { index ->
                cacheKeys[index] in existingKeys
            }
        }
    }
}
