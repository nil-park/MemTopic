package com.nolbee.memtopic.topic_list_view

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.nolbee.memtopic.account_view.SecureKeyValueStore
import com.nolbee.memtopic.client.TextToSpeechGCP
import com.nolbee.memtopic.database.Topic
import com.nolbee.memtopic.database.sampleTopic00
import com.nolbee.memtopic.ui.theme.MemTopicTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun TopicListItem(topic: Topic) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Column {
        ListItem(
            headlineContent = { Text(topic.title) },
            supportingContent = {
                Text(
                    topic.content,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            trailingContent = {
                Icon(Icons.Filled.Edit, contentDescription = null)
            },
            leadingContent = {
                IconButton(onClick = {
                    coroutineScope.launch {
                        try {
                            // TODO: API 키를 keystore에서 가져오기. 그리고 play는 여기서 할 것이 아님...
                            val keyValueStore = SecureKeyValueStore(context)
                            val client = TextToSpeechGCP(
                                keyValueStore.get("gcpTextToSpeechToken") ?: "",
                                "en-US",
                                "en-US-Neural2-J"
                            )
                            val audioBase64 = client.synthesize(topic.content)
                            withContext(Dispatchers.Main) {
                                play(audioBase64)
                            }
                        } catch (e: Exception) {
                            Log.d("GCPTest", "Error from synthesize(): $e")
                        }
                    }
                }) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                    // Icon(Icons.Filled.Download, contentDescription = null)
                }
            }
        )
        HorizontalDivider()
    }
}

@Preview
@Composable
fun TopicListItemPreview() {
    MemTopicTheme {
        TopicListItem(sampleTopic00)
    }
}

private fun play(audioBase64: String) {
    val player = MediaPlayer()
    try {
        player.reset()
        player.setDataSource("data:audio/mp3;base64,$audioBase64")
        player.prepare()
        player.start()
    } catch (e: Exception) {
        Log.d("GCPTest", "Error from play(): ${e.message}")
    }
}
