package com.nolbee.memtopic.player

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.nolbee.memtopic.database.Playback
import com.nolbee.memtopic.settings.Settings
import kotlin.math.max

private enum class PlayerSoundType {
    PRE_INTERVAL,
    TTS_SOUND,
    POST_INTERVAL
}

class MediaPlayerWithIntervalSound(
    private val applicationContext: Context,
    private val onCompletion: () -> Unit,
) {
    private val mediaPlayer: MediaPlayer = MediaPlayer().apply {
        setOnErrorListener { _, what, extra ->
            Log.e("MediaPlayerWithIntervalSound", "MediaPlayer Error: what=$what, extra=$extra")
            false
        }
    }

    private var lastAppliedAudioBase64 = ""
    private var lastCalculatedTTSDuration: Int = 0
    private var lastAppliedSettings = Settings()
    private var lastPlayedSoundType = PlayerSoundType.POST_INTERVAL

    private val onCompletionListener = MediaPlayer.OnCompletionListener {
        when (lastPlayedSoundType) {
            PlayerSoundType.PRE_INTERVAL -> {
                lastPlayedSoundType = PlayerSoundType.TTS_SOUND
                playTTSSound()
            }

            PlayerSoundType.TTS_SOUND -> {
                if (lastAppliedSettings.postIntervalMultiplier > 0f) {
                    lastPlayedSoundType = PlayerSoundType.POST_INTERVAL
                    playIntervalSound(
                        (lastCalculatedTTSDuration * lastAppliedSettings.postIntervalMultiplier).toInt(),
                        "flowing_stream.mp3"
                    )
                } else {
                    onCompletion()
                }
            }

            PlayerSoundType.POST_INTERVAL -> {
                onCompletion()
            }
        }
    }

    private fun calculateAudioBase64Duration(audioBase64: String): Int {
        mediaPlayer.reset()
        mediaPlayer.setDataSource("data:audio/mp3;base64,$audioBase64")
        mediaPlayer.prepare()
        val durationMs = mediaPlayer.duration
        Log.d("MediaPlayerWithIntervalSound", "audioBase64 duration: $durationMs ms")
        return durationMs
    }

    private fun playTTSSound() {
        mediaPlayer.apply {
            reset()
            setDataSource("data:audio/mp3;base64,$lastAppliedAudioBase64")
            prepare()
            setVolume(1.0f, 1.0f)
            playbackParams = playbackParams.setSpeed(lastAppliedSettings.playbackSpeed)
            setOnCompletionListener(onCompletionListener)
            start()
        }
    }

    private fun playIntervalSound(intervalDuration: Int, filename: String) {
        val fd = applicationContext.assets.openFd(filename)
        mediaPlayer.reset()
        mediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
        fd.close()
        mediaPlayer.prepare()
        val offset = max(mediaPlayer.duration - intervalDuration, 0)
        Log.d(
            "MediaPlayerWithIntervalSound",
            "Interval sound offset: $offset out of ${mediaPlayer.duration}"
        )
        mediaPlayer.seekTo(offset)
        mediaPlayer.setVolume(0.3f, 0.3f)
        mediaPlayer.setOnCompletionListener(onCompletionListener)
        mediaPlayer.start()
    }

    fun play(audioBase64: String, playback: Playback, settings: Settings) {
        lastAppliedSettings = settings
        lastCalculatedTTSDuration = calculateAudioBase64Duration(audioBase64)
        lastAppliedAudioBase64 = audioBase64
        if (settings.preIntervalMultiplier > 0f && playback.currentRepetition == 0) {
            lastPlayedSoundType = PlayerSoundType.PRE_INTERVAL
            playIntervalSound(
                (lastCalculatedTTSDuration * settings.preIntervalMultiplier).toInt(),
                "quartz_clock.mp3"
            )
        } else {
            lastPlayedSoundType = PlayerSoundType.TTS_SOUND
            playTTSSound()
        }
    }

    fun stop() {
        mediaPlayer.takeIf { it.isPlaying }?.stop()
    }

    fun release() {
        mediaPlayer.setOnCompletionListener(null)
        mediaPlayer.setOnErrorListener(null)
        mediaPlayer.takeIf { it.isPlaying }?.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
    }
}
