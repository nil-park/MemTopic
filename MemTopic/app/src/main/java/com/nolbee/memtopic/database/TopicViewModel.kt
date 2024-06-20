package com.nolbee.memtopic.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TopicViewModel(private val repository: TopicRepository) : ViewModel() {
    private val sortType = MutableStateFlow(TopicSortType.LAST_PLAYBACK)

    val topics: Flow<List<Topic>> = sortType.flatMapLatest { sort ->
        repository.getTopicList(sort)
    }

    fun addTopic(topic: Topic) {
        viewModelScope.launch {
            repository.upsertTopic(topic)
        }
    }

    fun deleteTopic(topic: Topic) {
        viewModelScope.launch {
            repository.deleteTopic(topic)
        }
    }
}
