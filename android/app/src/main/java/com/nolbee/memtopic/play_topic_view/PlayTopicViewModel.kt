package com.nolbee.memtopic.play_topic_view

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nolbee.memtopic.database.AudioCacheRepository
import com.nolbee.memtopic.database.PlaybackRepository
import com.nolbee.memtopic.database.Topic
import com.nolbee.memtopic.utils.ContentParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface IPlayTopicViewModel {
    val topicToPlay: Topic
    val playableLines: MutableStateFlow<List<String>>
    val isCachedLines: MutableStateFlow<List<Boolean>>
    val currentLineIndex: MutableStateFlow<Int>
    fun setTopic(topic: Topic)
    fun setCurrentLine(index: Int)
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlayTopicViewModel @Inject constructor(
    private val playbackRepository: PlaybackRepository,
    private val audioCacheRepository: AudioCacheRepository,
) : ViewModel(), IPlayTopicViewModel {
    override var topicToPlay: Topic by mutableStateOf(Topic())
        private set

    override var playableLines = MutableStateFlow<List<String>>(emptyList())
        private set

    override var isCachedLines = MutableStateFlow<List<Boolean>>(emptyList())
        private set

    override var currentLineIndex = MutableStateFlow(-1)
        private set

    init {
        viewModelScope.launch {
            playbackRepository.getPlayback().collect { playback ->
                playback?.let { p ->
                    if (p.topicId != topicToPlay.id)
                        currentLineIndex.value = -1
                    else
                        currentLineIndex.value = p.sentenceIndex
                    Log.d("PlayTopicViewModel", "currentLineIndex: ${currentLineIndex.value}")
                }
            }
        }
        viewModelScope.launch {
            playableLines
                .flatMapLatest { lines -> audioCacheRepository.getIsCachedLines(lines) }
                .collect { list ->
                    isCachedLines.value = list
                    Log.d("PlayTopicViewModel", "isCachedLines: $list")
                }
        }
    }

    override fun setTopic(topic: Topic) {
        this.topicToPlay = topic
        val sentences = ContentParser.parseContentToSentences(topic.content)
        playableLines.update { sentences }
        // TODO: If audio player service is not running, setCurrentLine(0)
    }

    override fun setCurrentLine(index: Int) {
        if (index in playableLines.value.indices) {
            viewModelScope.launch {
                playbackRepository.setCurrentLine(index)
            }
        }
    }
}

class MockPlayTopicViewModel : ViewModel(), IPlayTopicViewModel {
    override var topicToPlay: Topic by mutableStateOf(Topic())
        private set

    override var playableLines = MutableStateFlow<List<String>>(emptyList())
        private set

    override var isCachedLines = MutableStateFlow<List<Boolean>>(emptyList())
        private set

    override var currentLineIndex = MutableStateFlow(0)
        private set

    override fun setTopic(topic: Topic) {
        this.topicToPlay = topic
        val sentences = ContentParser.parseContentToSentences(topic.content)
        playableLines.update { sentences }
        val lines = MutableList(minOf(2, sentences.size)) { true }
        if (sentences.size > 1) lines[1] = false
        isCachedLines.update { lines }
        setCurrentLine(0)
    }

    override fun setCurrentLine(index: Int) {
        if (index in playableLines.value.indices) {
            currentLineIndex.value = index
        }
    }
}
