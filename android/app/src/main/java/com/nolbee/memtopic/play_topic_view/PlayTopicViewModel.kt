package com.nolbee.memtopic.play_topic_view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nolbee.memtopic.database.PlaybackRepository
import com.nolbee.memtopic.database.Topic
import com.nolbee.memtopic.utils.ContentParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface IPlayTopicViewModel {
    val topicToPlay: Topic
    val playableLines: MutableStateFlow<List<String>>
    val currentLineIndex: MutableStateFlow<Int>
    fun setTopic(topic: Topic)
    fun setCurrentLine(index: Int)
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlayTopicViewModel @Inject constructor(
    private val repository: PlaybackRepository
) : ViewModel(), IPlayTopicViewModel {
    override var topicToPlay: Topic by mutableStateOf(Topic())
        private set

    override var playableLines = MutableStateFlow<List<String>>(emptyList())
        private set

    override var currentLineIndex = MutableStateFlow(0)
        private set

    init {
        viewModelScope.launch {
            repository.getPlayback().collect { playback ->
                playback?.let { p ->
                    currentLineIndex.value = p.sentenceIndex
                    // TODO: Update topicToPlay from DB
                }
            }
        }
    }

    override fun setTopic(topic: Topic) {
        this.topicToPlay = topic
        val sentences = ContentParser.parseContentToSentences(topic.content)
        playableLines.update { sentences }
        setCurrentLine(0)
    }

    override fun setCurrentLine(index: Int) {
        if (index in playableLines.value.indices) {
            viewModelScope.launch {
                repository.setCurrentLine(index)
            }
        }
    }
}

class MockPlayTopicViewModel : ViewModel(), IPlayTopicViewModel {
    override var topicToPlay: Topic by mutableStateOf(Topic())
        private set

    override var playableLines = MutableStateFlow<List<String>>(emptyList())
        private set

    override var currentLineIndex = MutableStateFlow(0)
        private set

    override fun setTopic(topic: Topic) {
        this.topicToPlay = topic
        val sentences = ContentParser.parseContentToSentences(topic.content)
        playableLines.update { sentences }
        setCurrentLine(0)
    }

    override fun setCurrentLine(index: Int) {
        if (index in playableLines.value.indices) {
            currentLineIndex.value = index
        }
    }
}
