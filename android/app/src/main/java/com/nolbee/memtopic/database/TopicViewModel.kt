package com.nolbee.memtopic.database

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ITopicViewModel {
    val topics: Flow<List<Topic>>
    var topicToEdit: Topic
    var topicToPlay: Topic
    fun upsertTopic(topic: Topic)
    fun deleteTopic()
    var isDeleteConfirmDialogOpen: Boolean
    var topicToDelete: Topic
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class TopicViewModel @Inject constructor(
    private val repository: TopicRepository
) : ViewModel(), ITopicViewModel {
    private val sortType = MutableStateFlow(TopicSortType.LAST_PLAYBACK)

    override val topics: Flow<List<Topic>> = sortType.flatMapLatest { sort ->
        repository.getTopicList(sort)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    override var topicToEdit: Topic by mutableStateOf(Topic())
    override var topicToPlay: Topic by mutableStateOf(Topic())

    override fun upsertTopic(topic: Topic) {
        viewModelScope.launch {
            repository.upsertTopic(topic)
        }
    }

    override fun deleteTopic() {
        viewModelScope.launch {
            repository.deleteTopic(topicToDelete)
        }
    }

    override var isDeleteConfirmDialogOpen: Boolean by mutableStateOf(false)
    override var topicToDelete: Topic by mutableStateOf(Topic())
}

class MockTopicViewModel : ViewModel(), ITopicViewModel {
    private val _topics = MutableStateFlow(
        listOf(sampleTopic00, sampleTopic01)
    )
    override val topics: Flow<List<Topic>> = _topics.asStateFlow()
    override var topicToEdit = Topic()
    override var topicToPlay = Topic()

    override fun upsertTopic(topic: Topic) {}
    override fun deleteTopic() {}
    override var isDeleteConfirmDialogOpen: Boolean = false
    override var topicToDelete = Topic()
}
