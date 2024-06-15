package com.nolbee.memtopic

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.nolbee.memtopic.client.TextToSpeechGCP
import com.nolbee.memtopic.database.Topic
import com.nolbee.memtopic.ui.theme.MemTopicTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TopicListItem(topic: Topic) {
    val coroutineScope = rememberCoroutineScope()
    Column {
        ListItem(
            headlineContent = { Text(topic.name) },
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
                            val client = TextToSpeechGCP("", "en-US", "en-US-Neural2-J")
                            val audioBase64 = client.Synthesize(topic.content)
                            withContext(Dispatchers.Main) {
                                play(audioBase64)
                            }
                        } catch (e: Exception) {
                            Log.d("GCPTest", "Error from synthesize(): $e")
                        }
                    }
                }) {
                    Icon(Icons.Filled.Download, contentDescription = null)
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
        TopicListItem(sampleTopic)
    }
}

private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
private val lastModified: Date = dateFormat.parse("2023-09-12") ?: Date()
private val lastPlayback: Date = dateFormat.parse("2023-09-28") ?: Date()
val sampleTopic = Topic(
    name = "Insertion Sort",
    lastModified = lastModified,
    lastPlayback = lastPlayback,
    content = """
        We start with insertion sort, which is an efficient algorithm for sorting a small number of elements.
        Insertion sort works the way many people sort a hand of playing cards.
        We start with an empty left hand and the cards face down on the table.
        We then remove one card at a time from the table
        and insert it into the correct position in the left hand.
        To find the correct position for a card,
        we compare it with each of the cards already in the hand,
        from right to left, as illustrated in Figure 2.1.
        At all times, the cards held in the left hand are sorted,
        and these cards were originally the top cards of the pile on the table.
    """.trimIndent()
)

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
