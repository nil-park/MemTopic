package com.nolbee.memtopic

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nolbee.memtopic.utils.AudioPlayerHelper.calculateAudioDuration
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class AudioPlayerHelperTest {
    @Test
    fun calculateAudioDurationTest() {
        val appContext = InstrumentationRegistry.getInstrumentation().context
        val audioBase64: String
        appContext.assets.open("HelloWorld.txt").use { inputStream ->
            audioBase64 = inputStream.bufferedReader().readText()
        }
        val duration = calculateAudioDuration(audioBase64)
        Log.d("androidTest", "Audio duration: $duration")
        assertEquals(duration.toMillis(), 1128)
    }
}
