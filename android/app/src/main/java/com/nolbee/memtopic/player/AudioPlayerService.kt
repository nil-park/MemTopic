package com.nolbee.memtopic.player

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle

class AudioPlayerService : Service() {
    companion object {
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_EXIT = "ACTION_EXIT"
        const val KEY_BASE64_AUDIO = "KEY_BASE64_AUDIO"
        private const val CHANNEL_ID = "MediaPlaybackChannel"
        private const val NOTIFICATION_ID = 100
    }

    private var mediaPlayer: MediaPlayer? = null
    private var base64Data: String = ""

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaPlayer = MediaPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                base64Data = intent.getStringExtra(KEY_BASE64_AUDIO) ?: return START_NOT_STICKY
                play()
            }

            ACTION_EXIT -> {
                stopMedia()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
        }
        startForeground(NOTIFICATION_ID, buildNotification())
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

    private fun stopMedia() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
        .setContentTitle("Audio Player")
        .setContentText("Playing Audio")
        .setStyle(MediaStyle())
        .setOngoing(true)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .addAction(
            NotificationCompat.Action(
                android.R.drawable.ic_media_play,
                "Play",
                PendingIntent.getService(
                    this, 0,
                    Intent(this, AudioPlayerService::class.java).apply { action = ACTION_PLAY },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        )
        .addAction(
            NotificationCompat.Action(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Exit",
                PendingIntent.getService(
                    this, 0,
                    Intent(this, AudioPlayerService::class.java).apply { action = ACTION_EXIT },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        )
        .build()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Media Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }
}
