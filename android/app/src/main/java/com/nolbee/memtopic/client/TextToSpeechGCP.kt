package com.nolbee.memtopic.client

import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private data class TextToSpeechGCPAudioConfig(
    var audioEncoding: String = "MP3", // LINEAR16, MP3, MP3_64_KBPS, OGG_OPUS, MULAW, ALAW
    var speakingRate: Double = 1.0, // [0.25, 4.0]
    var pitch: Double = 0.0, // [-20.0, 20.0]
    var volumeGainDb: Double = 0.0, // [-96.0, 16.0]
    var sampleRateHertz: Int,
    var effectsProfileId: List<String>
)

private data class TextToSpeechGCPRequestAudioConfig(
    var audioEncoding: String = "MP3",
    var pitch: Double = 0.0,
    var speakingRate: Double = 1.0
)

private data class TextToSpeechGCPRequestVoice(
    var languageCode: String,
    var name: String
)

private data class TextToSpeechGCPRequestInput(
    var text: String
)

private data class TextToSpeechGCPRequest(
    var audioConfig: TextToSpeechGCPRequestAudioConfig,
    var input: TextToSpeechGCPRequestInput,
    var voice: TextToSpeechGCPRequestVoice
)

private data class TextToSpeechGCPResponseTimePoint(
    var markName: String,
    var timeSeconds: Double
)

private data class TextToSpeechGCPResponse(
    var audioContent: String,
    var timepoints: List<TextToSpeechGCPResponseTimePoint>,
    var audioConfig: TextToSpeechGCPAudioConfig
)

private suspend fun extractErrorMessage(res: HttpResponse) {
    if (res.status.value != 200) {
        val body = res.bodyAsText()
        var msg = "(${res.status}): $body"
        throw java.lang.Exception(msg)
//        try {
//            msg = Gson().fromJson(body, RemoteResError::class.java).msg
//        } finally {
//            throw java.lang.Exception(msg)
//        }
    }
}

private const val HOST = "https://texttospeech.googleapis.com"

class TextToSpeechGCP(
    private val apiKey: String,
    languageCode: String,
    voiceType: String
) {

    private var payload = TextToSpeechGCPRequest(
        TextToSpeechGCPRequestAudioConfig(),
        TextToSpeechGCPRequestInput("Hello World!"),
        TextToSpeechGCPRequestVoice(languageCode, voiceType)
    )

    private suspend fun synthesize(): String {
        val response: HttpResponse
        val url = "$HOST/v1beta1/text:synthesize"
        withContext(Dispatchers.IO) {
            try {
                HttpClient(CIO) {
                    install(HttpTimeout) {
                        requestTimeoutMillis = 15000 // TODO: request timeout from configuration
                    }
                }.use { client ->
                    response = client.post(url) {
                        header("X-goog-api-key", apiKey)
                        contentType(ContentType.Application.Json)
                        setBody(Gson().toJson(payload))
                    }
                }
            } catch (e: Exception) {
                throw e
            }
        }
        extractErrorMessage(response)
        val audioBase64 = Gson().fromJson(
            response.bodyAsText(),
            TextToSpeechGCPResponse::class.java
        ).audioContent
        return audioBase64
    }

    suspend fun synthesize(text: String): String {
        payload.input.text = text
        return synthesize()
    }

}