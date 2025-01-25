package com.nolbee.memtopic.player

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class AudioPlayerService : Service() {
    companion object {
        const val ACTION_PLAY = "ACTION_PLAY"
        const val KEY_BASE64_AUDIO = "KEY_BASE64_AUDIO"
    }

    private var mediaPlayer: MediaPlayer? = null
    private var base64Data: String = ""

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                base64Data = intent.getStringExtra(KEY_BASE64_AUDIO) ?: return START_NOT_STICKY
                play()
            }
        }
        return START_NOT_STICKY
    }

    private fun play() {
        if (mediaPlayer?.isPlaying == true) return

        mediaPlayer?.apply {
            reset()
            setDataSource("data:audio/mp3;base64,$base64Data")
            prepare()
            start()
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
