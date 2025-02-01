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
import com.nolbee.memtopic.account_view.SecureKeyValueStore
import com.nolbee.memtopic.client.TextToSpeechGCP
import com.nolbee.memtopic.database.Playback
import com.nolbee.memtopic.database.PlaybackDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// TODO: Notification 권한 요청

@AndroidEntryPoint
class AudioPlayerService : Service() {
    companion object {
        const val ACTION_UPDATE = "ACTION_UPDATE"
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_EXIT = "ACTION_EXIT"
        const val KEY_TOPIC_ID = "KEY_TOPIC_ID"
        const val KEY_CONTENT = "KEY_CONTENT"
        private const val CHANNEL_ID = "MediaPlaybackChannel"
        private const val NOTIFICATION_ID = 100
    }

    @Inject
    lateinit var playbackDao: PlaybackDao

    private var mediaPlayer: MediaPlayer? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaPlayer = MediaPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_UPDATE -> {
                val topicId = intent.getIntExtra(KEY_TOPIC_ID, -1)
                val content = intent.getStringExtra(KEY_CONTENT).orEmpty()
                serviceScope.launch {
                    playbackDao.upsertPlayback(
                        Playback(
                            topicId = topicId,
                            sentenceIndex = 0,
                            currentRepetition = 0,
                            totalRepetitions = 2,
                            isInterval = false,
                            content = content,
                        )
                    )
                    try {
                        val keyValueStore = SecureKeyValueStore(applicationContext)
                        val client = TextToSpeechGCP(
                            keyValueStore.get("gcpTextToSpeechToken") ?: "",
                            "en-US",
                            "en-US-Neural2-J"
                        )
                        val audioBase64 = client.synthesize(content)
                        withContext(Dispatchers.Main) {
                            play(audioBase64)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            ACTION_PLAY -> {
                serviceScope.launch {
                    try {
                        val playback = playbackDao.getPlaybackOnce()
                        val content = playback?.content ?: ""
                        val keyValueStore = SecureKeyValueStore(applicationContext)
                        val client = TextToSpeechGCP(
                            keyValueStore.get("gcpTextToSpeechToken") ?: "",
                            "en-US",
                            "en-US-Neural2-J"
                        )
                        val audioBase64 = client.synthesize(content)
                        withContext(Dispatchers.Main) {
                            play(audioBase64)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
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

    private fun play(audioBase64: String) {
        if (mediaPlayer?.isPlaying == true) return

        mediaPlayer?.apply {
            reset()
            setDataSource("data:audio/mp3;base64,$audioBase64")
            prepare()
            start()
        }
    }

    private fun stopMedia() {
        mediaPlayer?.takeIf { it.isPlaying }?.stop()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        serviceScope.cancel()
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
