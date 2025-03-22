package com.nolbee.memtopic.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Base64
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import java.io.File
import java.io.FileOutputStream
import java.time.Duration

object AudioPlayerHelper {
    fun calculateAudioDuration(audioBase64: String): Duration {
        val mediaPlayer = MediaPlayer().apply {
            reset()
            setDataSource("data:audio/mp3;base64,$audioBase64")
            prepare()
        }
        val durationMs = mediaPlayer.duration
        mediaPlayer.release()
        return Duration.ofMillis(durationMs.toLong())
    }

    fun calculateAudioDuration(audioFile: File): Duration {
        val mediaPlayer = MediaPlayer().apply {
            reset()
            setDataSource(audioFile.absolutePath)
            prepare()
        }
        val durationMs = mediaPlayer.duration
        mediaPlayer.release()
        return Duration.ofMillis(durationMs.toLong())
    }

    private fun saveBase64ToMp3(base64String: String, context: Context): File {
        val base64RawFile = File(context.cacheDir, "base64Raw.mp3").apply { deleteOnExit() }
        FileOutputStream(base64RawFile).use {
            it.write(
                Base64.decode(
                    base64String,
                    Base64.DEFAULT
                )
            )
        }
        return base64RawFile
    }

    private fun changeMp3Bitrate(audioFile: File, context: Context): File {
        val base64Fixed = File(context.cacheDir, "base64Fixed.mp3").apply { deleteOnExit() }
        val cmd =
            "-y -i ${audioFile.absolutePath} -c:a libmp3lame -q:a 2 ${base64Fixed.absolutePath}"
        val session = FFmpegKit.execute(cmd)
        assert(ReturnCode.isSuccess(session.returnCode))  // TODO: Raise exception if and handle it
        return base64Fixed
    }

    private fun copyIntervalSoundToCacheDir(context: Context): File {
        val intervalSoundFile =
            File(context.cacheDir, "flowing_stream.mp3").apply { deleteOnExit() }
        if (intervalSoundFile.exists() && intervalSoundFile.length() == 2756022L) {
            return intervalSoundFile
        }
        context.assets.open("flowing_stream.mp3").use { input ->
            FileOutputStream(intervalSoundFile).use { output -> input.copyTo(output) }
        }
        return intervalSoundFile
    }

    private fun cutIntervalSound(durationInMillis: Long, context: Context): File {
        val intervalSoundFile = copyIntervalSoundToCacheDir(context)
        val intervalSoundFileShort =
            File(context.cacheDir, "flowing_stream_short.mp3").apply { deleteOnExit() }
        val cmd =
            "-y -i ${intervalSoundFile.absolutePath} -t ${durationInMillis}ms -c:a libmp3lame -q:a 2 ${intervalSoundFileShort.absolutePath}"
        val session = FFmpegKit.execute(cmd)
        assert(ReturnCode.isSuccess(session.returnCode))  // TODO: Raise exception if and handle it
        return intervalSoundFileShort
    }

    private fun concatAudioFiles(f1: File, f2: File, context: Context): File {
        val mergedFile = File(context.cacheDir, "merged.mp3").apply { deleteOnExit() }
        val cmd =
            "-y -i \"concat:${f1.absolutePath}|${f2.absolutePath}\" -c:a libmp3lame -q:a 2 ${mergedFile.absolutePath}"
        val session = FFmpegKit.execute(cmd)
        assert(ReturnCode.isSuccess(session.returnCode))  // TODO: Raise exception if and handle it
        return mergedFile
    }

    fun appendIntervalSound(audioBase64: String, context: Context, multiplier: Float = 1.0f): File {
        Log.d("AudioPlayerHelper", "Original Duration: ${calculateAudioDuration(audioBase64)}")
        val ttsSoundFile = saveBase64ToMp3(audioBase64, context)
        val ttsSoundFileFixedBitrate = changeMp3Bitrate(ttsSoundFile, context)
        val duration =
            (calculateAudioDuration(ttsSoundFileFixedBitrate).toMillis() * multiplier).toLong()
        val intervalSoundFileShort = cutIntervalSound(duration, context)
        val mergedFile = concatAudioFiles(ttsSoundFileFixedBitrate, intervalSoundFileShort, context)
        Log.d(
            "AudioPlayerHelper",
            "Interval Sound Added Duration: ${calculateAudioDuration(mergedFile)}"
        )
        return mergedFile
    }
}
