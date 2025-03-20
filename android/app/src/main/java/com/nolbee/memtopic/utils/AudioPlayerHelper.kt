package com.nolbee.memtopic.utils

import android.media.MediaPlayer
import java.io.File
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
}
