package com.nolbee.memtopic.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.nolbee.memtopic.database.AudioCacheDao
import com.nolbee.memtopic.database.PlaybackDao
import com.nolbee.memtopic.database.TopicDao
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudioPlayerService : Service() {
    companion object {
        const val ACTION_UPDATE = "ACTION_UPDATE"
        const val ACTION_EXIT = "ACTION_EXIT"
        const val KEY_TOPIC_ID = "KEY_TOPIC_ID"
        const val KEY_SENTENCE_ID = "KEY_SENTENCE_ID"
        private const val CHANNEL_ID = "MediaPlaybackChannel"
        private const val NOTIFICATION_ID = 100
    }

    @Inject
    lateinit var playbackDao: PlaybackDao

    @Inject
    lateinit var topicDao: TopicDao

    @Inject
    lateinit var audioCacheDao: AudioCacheDao

    private lateinit var audioPlayer: AudioPlayer

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        audioPlayer = AudioPlayer(playbackDao, topicDao, audioCacheDao, applicationContext) {
            updateNotification()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_UPDATE -> {
                val topicId = intent.getIntExtra(KEY_TOPIC_ID, -1)
                val sentenceIndex = intent.getIntExtra(KEY_SENTENCE_ID, -1)
                Log.d("AudioPlayerService", "topicId: $topicId, sentenceIndex: $sentenceIndex")
                audioPlayer.play(topicId, sentenceIndex)
            }

            ACTION_EXIT -> {
                audioPlayer.stop()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
        }
        startForeground(NOTIFICATION_ID, buildNotification())
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        audioPlayer.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun updateNotification() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun buildNotification(): Notification {
        val exitAction = NotificationCompat.Action(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Exit",
            PendingIntent.getService(
                this, 0,
                Intent(this, AudioPlayerService::class.java).apply { action = ACTION_EXIT },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
            .setContentTitle(audioPlayer.notificationTitle)
            .setContentText(audioPlayer.notificationText)
            .setStyle(MediaStyle().setShowActionsInCompactView(0))
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(0)
            .setSound(null)
            .setVibrate(null)
            .setSilent(true)
            .addAction(exitAction)
            .setShowWhen(false)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Media Playback",
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }
}
