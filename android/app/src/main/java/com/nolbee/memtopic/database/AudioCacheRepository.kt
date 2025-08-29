package com.nolbee.memtopic.database

import android.content.Context
import android.util.Log
import com.nolbee.memtopic.R
import com.nolbee.memtopic.client.TextToSpeechGCP
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import javax.inject.Inject

class AudioCacheRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioCacheDao: AudioCacheDao
) {

    fun getIsCachedLines(lines: List<String>, voiceOptions: String): Flow<List<Boolean>> {
        val jsonObject = JSONObject(voiceOptions)
        val defaultLanguageCode = context.getString(R.string.default_gcp_language_code)
        val defaultVoiceType = context.getString(R.string.default_gcp_voice_name)
        val languageCode = jsonObject.optString("languageCode", defaultLanguageCode)
        val voiceType = jsonObject.optString("voiceType", defaultVoiceType)

        Log.d(
            "AudioCacheRepository",
            "voiceOptions: $voiceOptions, languageCode: $languageCode, voiceType: $voiceType"
        )

        val cacheKeys = lines.map { line ->
            TextToSpeechGCP.makeCacheKey(languageCode, voiceType, line)
        }

        return audioCacheDao.getCachedAudioByKeys(cacheKeys).map { cachedList ->
            val existingKeys = cachedList.map { it.cacheKey }.toSet()
            List(lines.size) { i -> cacheKeys[i] in existingKeys }
        }
    }
}