package com.nolbee.memtopic.play_topic_view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FileDownloadDone
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.nolbee.memtopic.database.sampleTopic02
import com.nolbee.memtopic.player.AudioPlayerService
import com.nolbee.memtopic.ui.theme.MemTopicTheme


@Composable
fun PlayableLineList(
    vm: IPlayTopicViewModel
) {
    val lines by vm.playableLines.collectAsState(initial = emptyList())
    val currentIndex by vm.currentLineIndex.collectAsState()
    val isCachedLines by vm.isCachedLines.collectAsState(emptyList())

    LazyColumn {
        itemsIndexed(lines) { index, text ->
            PlayableLineItem(
                index = index,
                text = text,
                isPlaying = currentIndex == index,
                isCached = isCachedLines.getOrNull(index),
                vm = vm,
            )
        }
    }
}

@Preview
@Composable
fun PlayableLineListPreview() {
    val vm = MockPlayTopicViewModel()
    vm.setTopic(sampleTopic02)
    MemTopicTheme {
        PlayableLineList(
            vm = vm
        )
    }
}

@Composable
fun PlayableLineItem(
    index: Int,
    text: String,
    isPlaying: Boolean,
    isCached: Boolean?,
    vm: IPlayTopicViewModel,
) {
    val context = LocalContext.current

    // Preparing notification permission launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startAudioService(context, vm, index)
        } else {
            Log.d("PlayableLineItem", "Notification permission denied")
        }
    }

    var size by remember { mutableStateOf(IntSize.Zero) }

    val infiniteTransition = rememberInfiniteTransition()
    val offsetAnimX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val offsetAnimY by infiniteTransition.animateFloat(
        initialValue = size.height.toFloat(),
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val rainbowBrush = Brush.sweepGradient(
        listOf(
            Color.Red,
            Color.Yellow,
            Color.Green,
            Color.Cyan,
            Color.Blue,
            Color.Magenta,
            Color.Red
        ),
        center = Offset(offsetAnimX, offsetAnimY)
    )
    val borderModifier = if (isPlaying) {
        Modifier
            .border(BorderStroke(2.dp, rainbowBrush))
            .onSizeChanged { size = it }
    } else {
        Modifier
    }

    Column(
        modifier = borderModifier.fillMaxWidth()
    ) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    vm.setCurrentLine(index)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            startAudioService(context, vm, index)
                        } else {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        startAudioService(context, vm, index)
                    }
                },
            headlineContent = {
                Text(
                    text = text,
                    fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal
                )
            },
            leadingContent = {
                val icon = when (isCached) {
                    false -> Icons.Default.Cloud
                    true -> Icons.Default.FileDownloadDone
                    else -> Icons.Default.QuestionMark
                }
                val tint = when (isCached) {
                    false -> Color(0xFF8080A0)
                    true -> Color(0xFF00A000)
                    else -> Color(0xFFA06060)
                }
                Icon(imageVector = icon, contentDescription = null, tint = tint)
            }
        )
        HorizontalDivider()
    }
}

private fun startAudioService(
    context: Context,
    vm: IPlayTopicViewModel,
    index: Int
) {
    val intent = Intent(context, AudioPlayerService::class.java).apply {
        action = AudioPlayerService.ACTION_UPDATE
        putExtra(AudioPlayerService.KEY_TOPIC_ID, vm.topicToPlay.id)
        putExtra(AudioPlayerService.KEY_SENTENCE_ID, index)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
    } else {
        context.startService(intent)
    }
}
