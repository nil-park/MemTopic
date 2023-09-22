package com.nolbee.memtopic.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class TopicViewModel(
    private val dao: TopicDao
) : ViewModel() {
    private val _sortType = MutableStateFlow(TopicSortType.LAST_PLAYBACK)
    private val _topics = _sortType
        .flatMapLatest { sortType ->
            when (sortType) {
                TopicSortType.NAME -> dao.selectTopicByName()
                TopicSortType.LAST_MODIFIED -> dao.selectTopicByLastModified()
                TopicSortType.LAST_PLAYBACK -> dao.selectTopicByLastPlayback()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(TopicState())
    val state = combine(_state, _sortType, _topics) { state, sortType, topics ->
        state.copy(
            topics = topics,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TopicState())

    fun onEvent(event: TopicEvent) {
        when (event) {
            is TopicEvent.DeleteTopic -> {
                viewModelScope.launch {
                    dao.deleteTopic(event.topic)
                }
            }

            TopicEvent.HideDialog -> {
                _state.update {
                    it.copy(
                        isAddingTopic = false
                    )
                }
            }

            TopicEvent.SaveTopic -> {
                val name = state.value.name
                val lastModified = state.value.lastModified
                val lastPlayback = state.value.lastPlayback
                if (name.isBlank()) {
                    return
                }
                val topic = Topic(
                    name = name,
                    lastModified = lastModified,
                    lastPlayback = lastPlayback,
                    content = ""
                )
                viewModelScope.launch {
                    dao.upsertTopic(topic)
                }
                _state.update {
                    it.copy(
                        isAddingTopic = false,
                        name = "",
                        lastModified = Date(),
                        lastPlayback = Date()
                    )
                }
            }

            is TopicEvent.SetLastModified -> {
                _state.update {
                    it.copy(
                        lastModified = event.date
                    )
                }
            }

            is TopicEvent.SetLastPlayback -> {
                _state.update {
                    it.copy(
                        lastPlayback = event.date
                    )
                }
            }

            is TopicEvent.SetName -> {
                _state.update {
                    it.copy(
                        name = event.name
                    )
                }
            }

            TopicEvent.ShowDialog -> {
                _state.update {
                    it.copy(
                        isAddingTopic = true
                    )
                }
            }

            is TopicEvent.SortTopic -> {
                _sortType.value = event.sortType
            }
        }
    }
}