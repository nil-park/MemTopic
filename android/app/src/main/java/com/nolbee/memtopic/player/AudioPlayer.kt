package com.nolbee.memtopic.player

import android.content.Context
import android.util.Log
import com.nolbee.memtopic.account_view.SecureKeyValueStore
import com.nolbee.memtopic.client.TextToSpeechGCP
import com.nolbee.memtopic.database.AudioCache
import com.nolbee.memtopic.database.AudioCacheDao
import com.nolbee.memtopic.database.Playback
import com.nolbee.memtopic.database.PlaybackDao
import com.nolbee.memtopic.database.SettingsRepository
import com.nolbee.memtopic.database.TopicDao
import com.nolbee.memtopic.settings.Settings
import com.nolbee.memtopic.utils.ContentParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AudioPlayer(
    private val playbackDao: PlaybackDao,
    private val topicDao: TopicDao,
    private val audioCacheDao: AudioCacheDao,
    private val settingsRepository: SettingsRepository,
    private val applicationContext: Context,
    private val onUpdateNotification: () -> Unit,
) {
    private val mediaPlayerWithIntervalSound =
        MediaPlayerWithIntervalSound(applicationContext) { playAudioLoopEnd() }
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    var notificationTitle: String = "Audio Player"
    var notificationText: String = "Sentence: 0/0, Repetition: 0/0"

    private suspend fun getOrSynthesizeAudioForLine(playback: Playback): String {
        val topic = topicDao.getTopic(playback.topicId)
        if (topic == null) {
            val msg = "Topic not found for ID: ${playback.topicId}"
            Log.e("AudioPlayer", msg)
            notificationTitle = "Error"
            notificationText = msg
            throw Exception(msg)
        }
        val jsonObject = JSONObject(topic.options)
        val languageCode = jsonObject.optString("languageCode", "en-US")
        val voiceType = jsonObject.optString("voiceType", "en-US-Neural2-J")

        val sentences = ContentParser.parseContentToSentences(playback.content)
        val sentence = sentences[playback.sentenceIndex]

        val cacheKey = TextToSpeechGCP.makeCacheKey(languageCode, voiceType, sentence)
        val cached = audioCacheDao.getCachedAudio(cacheKey)
        if (cached != null) {
            return cached
        } else {
            val apiKey = SecureKeyValueStore(applicationContext).get("gcpTextToSpeechToken") ?: ""
            val audioBase64 = TextToSpeechGCP(apiKey).synthesize(sentence, languageCode, voiceType)
            audioCacheDao.upsertCache(AudioCache(cacheKey, audioBase64))
            return audioBase64
        }
    }

    private suspend fun updateCurrentPlayback(topicId: Int, sentenceIndex: Int): Playback {
        val topic = topicDao.getTopic(topicId)
        val settings = settingsRepository.getSettings()
        if (topic == null) {
            val msg = "Topic not found for ID: $topicId"
            Log.e("AudioPlayer", msg)
            notificationTitle = "Error"
            notificationText = msg
            throw Exception(msg)
        }
        val sentences = ContentParser.parseContentToSentences(topic.content)
        val sentenceIndexText = "Sentence: ${sentenceIndex + 1}/${sentences.size}"
        val repetitionsText = when (settings.sentenceReputation) {
            Int.MAX_VALUE -> "Repetition: 1"
            else -> "Repetition: 1/${settings.sentenceReputation}"
        }
        notificationTitle = topic.title
        notificationText = "$sentenceIndexText, $repetitionsText"
        val playback = Playback(
            topicId = topicId,
            sentenceIndex = sentenceIndex,
            currentRepetition = 0,
            totalRepetitions = settings.sentenceReputation,
            isInterval = false,
            content = topic.content,
        )
        playbackDao.upsertPlayback(playback)
        return playback
    }

    private fun playAudioLoopEnd() {
        serviceScope.launch {
            val settings = settingsRepository.getSettings()
            val currPlayback = playbackDao.getPlaybackOnce()
            if (currPlayback == null) {
                val msg = "Playback not found"
                Log.e("AudioPlayer", msg)
                notificationTitle = "Error"
                notificationText = msg
                return@launch
            }
            val nextPlayback = currPlayback.next(settings.sentenceReputation)
            val sentences = ContentParser.parseContentToSentences(nextPlayback.content)
            val sentenceIndex = nextPlayback.sentenceIndex
            val sentenceIndexText = "Sentence: ${sentenceIndex + 1}/${sentences.size}"
            val repetitionsText = when (settings.sentenceReputation) {
                Int.MAX_VALUE -> "Repetition: ${nextPlayback.currentRepetition + 1}"
                else -> "Repetition: ${nextPlayback.currentRepetition + 1}/${settings.sentenceReputation}"
            }
            notificationText = "$sentenceIndexText, $repetitionsText"
            playbackDao.upsertPlayback(nextPlayback)
            playAudioLoop(nextPlayback, settings)
        }
    }

    private suspend fun playAudioLoop(playback: Playback, settings: Settings) {
        val audioBase64 = getOrSynthesizeAudioForLine(playback)
        withContext(Dispatchers.Main) {
            onUpdateNotification()
            mediaPlayerWithIntervalSound.play(audioBase64, playback, settings)
        }
    }

    fun play(topicId: Int, sentenceIndex: Int) {
        serviceScope.launch(Dispatchers.Main) {
            mediaPlayerWithIntervalSound.stop()
        }
        serviceScope.launch {
            try {
                val playback = updateCurrentPlayback(topicId, sentenceIndex)
                val settings = settingsRepository.getSettings()
                playAudioLoop(playback, settings)
            } catch (_: Exception) {
                onUpdateNotification()
            }
        }
    }

    fun stop() {
        serviceScope.launch(Dispatchers.Main) {
            mediaPlayerWithIntervalSound.stop()
        }
    }

    fun release() {
        serviceScope.launch(Dispatchers.Main) {
            mediaPlayerWithIntervalSound.release()
        }
        serviceScope.cancel()
    }
}