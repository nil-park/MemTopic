package com.nolbee.memtopic.database

import java.util.Date

sealed interface TopicEvent {
    object SaveTopic : TopicEvent
    data class SetName(val name: String) : TopicEvent
    data class SetLastModified(val date: Date) : TopicEvent
    data class SetLastPlayback(val date: Date) : TopicEvent
    object ShowDialog : TopicEvent
    object HideDialog : TopicEvent
    data class SortTopic(val sortType: TopicSortType) : TopicEvent
    data class DeleteTopic(val topic: Topic) : TopicEvent
}
