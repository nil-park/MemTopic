package com.nolbee.memtopic

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nolbee.memtopic.utils.AudioPlayerHelper
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Duration

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class AudioPlayerHelperTest {
    @Test
    fun calculateAudioDurationTest() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val audioBase64: String
        context.assets.open("HelloWorld.txt").use { inputStream ->
            audioBase64 = inputStream.bufferedReader().readText()
        }
        val duration = AudioPlayerHelper.calculateAudioDuration(audioBase64)
        Log.d("AudioPlayerHelperTest", "Audio duration: $duration")
        assertEquals(duration.toMillis(), 1128)
    }

    @Test
    fun appendIntervalSoundTest() {
        var duration: Duration
        val context = InstrumentationRegistry.getInstrumentation().context
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val base64String = context.assets.open("HelloWorld.txt").bufferedReader().readText()
        duration = AudioPlayerHelper.calculateAudioDuration(base64String)
        Log.d("AudioPlayerHelperTest", "Original Audio duration: $duration")
        val mergedFile = AudioPlayerHelper.appendIntervalSound(base64String, appContext)
        duration = AudioPlayerHelper.calculateAudioDuration(mergedFile)
        Log.d("AudioPlayerHelperTest", "Concatenated Audio duration: $duration")
        assertEquals(duration.toMillis(), 2400)
    }
}
