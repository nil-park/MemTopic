package com.nolbee.memtopic

import android.util.Base64
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import com.nolbee.memtopic.utils.AudioPlayerHelper.calculateAudioDuration
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

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
        val duration = calculateAudioDuration(audioBase64)
        Log.d("androidTest", "Audio duration: $duration")
        assertEquals(duration.toMillis(), 1128)
    }

    @Test
    fun mergeBase64AndMp3() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        var duration: Long
        var session: FFmpegSession

        // 1. (Base64) -> base64Raw.mp3
        val base64String = context.assets.open("HelloWorld.txt").bufferedReader().readText()
        val base64RawFile = File(appContext.cacheDir, "base64Raw.mp3").apply { deleteOnExit() }
        FileOutputStream(base64RawFile).use {
            it.write(
                Base64.decode(
                    base64String,
                    Base64.DEFAULT
                )
            )
        }

        // 2. base64Raw.mp3를 재인코딩 -> base64Fixed.mp3
        val base64Fixed = File(appContext.cacheDir, "base64Fixed.mp3").apply { deleteOnExit() }
        val cmd =
            "-i ${base64RawFile.absolutePath} -c:a libmp3lame -q:a 2 ${base64Fixed.absolutePath}"
        session = FFmpegKit.execute(cmd)
        assert(ReturnCode.isSuccess(session.returnCode))
        duration = calculateAudioDuration(base64Fixed).toMillis()
        Log.d("androidTest", "Base64 duration: ${duration}")

        // 2) main/assets/flowing_stream.mp3 (긴 MP3) 파일로 복사
        val longMp3File = File(appContext.cacheDir, "flowing_stream.mp3").apply { deleteOnExit() }
        appContext.assets.open("flowing_stream.mp3").use { input ->
            FileOutputStream(longMp3File).use { output -> input.copyTo(output) }
        }
        duration = calculateAudioDuration(longMp3File).toMillis()
        Log.d("androidTest", "Long MP3 file duration: ${duration}")

        // 4) 첫 번째 파일 길이(ms) 구해서 longMp3File을 그만큼 트리밍
        duration = calculateAudioDuration(base64Fixed).toMillis()
        Log.d("androidTest", "Base64 duration: $duration")
        val trimmedFile = File(appContext.cacheDir, "trimmed.mp3").apply { deleteOnExit() }
        val trimCmd =
            "-i ${longMp3File.absolutePath} -t ${duration}ms -c:a libmp3lame -q:a 2 ${trimmedFile.absolutePath}"
        Log.d("androidTest", "Trim Cmd: $trimCmd")
        session = FFmpegKit.execute(trimCmd)
        assert(ReturnCode.isSuccess(session.returnCode))

        duration = calculateAudioDuration(trimmedFile).toMillis()
        Log.d("androidTest", "Trimmed Audio duration: $duration")

        // 5) concat: tempBase64.mp3 + trimmed.mp3 → merged.mp3
        val mergedFile = File(appContext.cacheDir, "merged.mp3").apply { deleteOnExit() }
        val concatCmd =
            "-i \"concat:${base64Fixed.absolutePath}|${trimmedFile.absolutePath}\" -c copy ${mergedFile.absolutePath}"
        Log.d("androidTest", "Concat Cmd: $concatCmd")
        session = FFmpegKit.execute(concatCmd)
        assert(ReturnCode.isSuccess(session.returnCode))

        // 최종 길이 확인
        duration = calculateAudioDuration(mergedFile).toMillis()
        Log.d("androidTest", "Concatenated Audio duration: $duration")
    }
}
