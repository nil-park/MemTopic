package com.nolbee.memtopic.play_topic_view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nolbee.memtopic.database.Topic
import com.nolbee.memtopic.utils.ContentParser
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
        val sentences = ContentParser.parseContentToSentences(topic.content)
        playableLines.update { sentences }
        setCurrentLine(0)
    }

    fun setCurrentLine(index: Int) {
        if (index in playableLines.value.indices) {
            currentLineIndex.value = index
        }
    }
}
