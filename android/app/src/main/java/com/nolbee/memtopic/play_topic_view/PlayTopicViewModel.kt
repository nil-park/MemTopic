package com.nolbee.memtopic.play_topic_view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nolbee.memtopic.database.Topic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class PlayTopicViewModel : ViewModel() {
    var topicToPlay: Topic by mutableStateOf(Topic())
        private set

    var playableLines = MutableStateFlow<List<String>>(emptyList())
        private set

    var currentLineIndex = MutableStateFlow(0)
        private set

    fun setTopic(topic: Topic) {
        this.topicToPlay = topic
        parseContentToLines(topic.content)
        setCurrentLine(0)
    }

    fun setCurrentLine(index: Int) {
        if (index in playableLines.value.indices) {
            currentLineIndex.value = index
        }
    }

    private fun parseContentToLines(content: String) {
        val sentences = mutableListOf<String>()
        var i0 = 0
        content.forEachIndexed { i, c ->
            when (c) {
                '.', '?', '!' -> {
                    if (
                        i + 1 < content.length && content[i + 1].isWhitespace()
                        || i + 1 == content.length
                    ) {
                        sentences.add(content.substring(i0, i + 1))
                        i0 = i + 1
                    }
                }

                '\n', '\r' -> {
                    sentences.add(content.substring(i0, i + 1))
                    i0 = i + 1
                }
            }
        }
        playableLines.update { sentences.filterNot { it.isBlank() }.map { it.trim() } }
    }
}
