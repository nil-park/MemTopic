package com.nolbee.memtopic.client

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Base64


class TextToSpeechGCPTest {
    @Test
    fun `test TTS client`() = runBlocking {
        val apiKey = System.getenv("GCP_TTS_API_KEY") ?: error("No credentials found")
        val client = TextToSpeechGCP(apiKey)
        val audioBase64 = client.synthesize("Hello World!")
        // println("Base64 String: $audioBase64")

        assertFalse("Audio Base64 string should not be empty", audioBase64.isEmpty())
        assertTrue(
            "Audio Base64 string should be longer than 100 characters (Got ${audioBase64.length})",
            audioBase64.length > 100
        )
        assertTrue(
            "Audio Base64 string should be shorter than 10000 characters (Got ${audioBase64.length})",
            audioBase64.length < 20000
        )
        try {
            Base64.getDecoder().decode(audioBase64)
            assertTrue("Audio Base64 string should be decodable", true)
        } catch (e: IllegalArgumentException) {
            assertTrue("Audio Base64 string should be decodable", false)
        }
    }

    @Test
    fun `list language codes`(): Unit = runBlocking {
        val apiKey = System.getenv("GCP_TTS_API_KEY") ?: error("No credentials found")
        val client = TextToSpeechGCP(apiKey)
        val languageCodes = client.listLanguageCodes()
        println("Language Codes: $languageCodes")
        assertTrue("Voices should not be empty", "en-US" in languageCodes)
    }

    @Test
    fun `list TTS voices`(): Unit = runBlocking {
        val apiKey = System.getenv("GCP_TTS_API_KEY") ?: error("No credentials found")
        val client = TextToSpeechGCP(apiKey)
        val voices = client.listVoices("ko-KR").map { it.name }
        println("Voices: $voices")
        assertTrue("Voices should not be empty", voices.isNotEmpty())
    }
}
