package com.nolbee.memtopic.client

import com.google.api.gax.core.NoCredentialsProvider
import com.google.api.gax.rpc.FixedHeaderProvider
import com.google.cloud.texttospeech.v1.AudioConfig
import com.google.cloud.texttospeech.v1.AudioEncoding
import com.google.cloud.texttospeech.v1.SynthesisInput
import com.google.cloud.texttospeech.v1.TextToSpeechClient
import com.google.cloud.texttospeech.v1.TextToSpeechSettings
import com.google.cloud.texttospeech.v1.Voice
import com.google.cloud.texttospeech.v1.VoiceSelectionParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Base64

class TextToSpeechGCP(apiKey: String) {
    private val settings: TextToSpeechSettings = TextToSpeechSettings.newBuilder()
        .setCredentialsProvider(NoCredentialsProvider.create())
        .setHeaderProvider(FixedHeaderProvider.create("X-Goog-Api-Key", apiKey))
        .setTransportChannelProvider(
            TextToSpeechSettings.defaultHttpJsonTransportProviderBuilder().build()
        )
        .build()

    suspend fun synthesize(
        text: String,
        languageCode: String = "en-US",
        voiceType: String = "en-US-Neural2-J"
    ): String {
        return withContext(Dispatchers.IO) {
            TextToSpeechClient.create(settings).use { client ->
                val input = SynthesisInput.newBuilder().setText(text).build()
                val voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(languageCode)
                    .setName(voiceType)
                    .build()
                val audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build()
                val response = client.synthesizeSpeech(input, voice, audioConfig)
                Base64.getEncoder().encodeToString(response.audioContent.toByteArray())
            }
        }
    }

    suspend fun listLanguageCodes(): List<String> {
        return withContext(Dispatchers.IO) {
            TextToSpeechClient.create(settings).use { client ->
                val response = client.listVoices("")
                response.voicesList.flatMap { it.languageCodesList }.distinct()
            }
        }
    }

    suspend fun listVoices(languageCode: String): List<Voice> {
        return withContext(Dispatchers.IO) {
            TextToSpeechClient.create(settings).use { client ->
                val response = client.listVoices(languageCode)
                response.voicesList
            }
        }
    }

    companion object {
        fun makeCacheKey(languageCode: String, voiceType: String, sentence: String): String {
            return "gcp_${languageCode}_${voiceType}_${sentence.hashCode()}"
        }
    }
}
